const express = require("express");
const bodyParser = require('body-parser');
const forge = require('node-forge');
const bigInt = require("big-integer");
const yargs = require("yargs");
const axios = require("axios");
const os = require("os");
const BC = require('./blockchain');

const INITIAL_DIFFICULTY = bigInt(2).pow(bigInt(256)).divide(bigInt(100000));
const MD = forge.md.sha256.create();

const config = {
    port: yargs.argv.port || 3000,
    peers: (yargs.argv.peers ? yargs.argv.peers.split(',') : []),
    text: yargs.argv.text || 'bctest',
	delay: yargs.argv.delay || 3000,
};

let hexToBigInt = (hex) => {
    return bigInt(hex, 16);
};

let bigIntToHex = (bigInt) => {
    return bigInt.toString(16);
};

let digestStrToHex = (str) => {
    MD.start();
    MD.update(str);
    return MD.digest().toHex();
};

let blockchain = BC.createBlockchain();

console.log(INITIAL_DIFFICULTY.toString() + "  <<<<<<<");

const app = express();

let nonce = bigInt.zero;
let currentDifficulty = INITIAL_DIFFICULTY;

if (config.peers.length > 0) {
    axios.get(config.peers[0] + "/getblocks?ancestor=" + BC.ROOT_HASH).then((result) => {
        let blocksStr = result.data;
        if (blocksStr) {
            blocksStr.split('\n').forEach((blockStr) => {
                if (blockStr) {
                    receiveBlock(blockStr);
                }
            });
        }
    }).catch((error) => {});
    config.peers.forEach((peer) => {
        axios.post(peer + "/addpeer", "peer=" + encodeURIComponent("http://" + os.hostname() + ":" + config.port),
            {headers: {"Content-Type": "application/x-www-form-urlencoded"}})
            .catch((error) => {});
    });
}

setInterval(() => {
    for(let i = 0; i < 1000; i++) {
        nonce = nonce.plus(bigInt.one);
        let block = BC.createBlock(blockchain.getTopBlockHash(), nonce.toString(), new Date().getTime(), config.text);
        let blockHash = digestStrToHex(block.blockContentsString());
        if (hexToBigInt(blockHash).lesserOrEquals(currentDifficulty)) {
            block.blockHash = blockHash;
            //console.log(hashBigInt.toString());
            blockchain.addBlock(block, true);
            relayBlock(block, config.delay);
        }
    }
}, 50);

let receiveBlock = (blockStr) => {
    let blockElms = blockStr.split(':');
    if (blockElms.length != 5) {
        console.error('Invalid number of elements in block');
        return;
    }

    let block = BC.createBlock(blockElms[1], blockElms[2], blockElms[3], blockElms[4]);
    block.blockHash = blockElms[0];

    let blockContentsStr = block.blockContentsString();
    let blockHash = digestStrToHex(blockContentsStr);
    if (block.blockHash !== blockHash) {
        console.error('Invalid block hash');
        return;
    }
    let hashBigInt = hexToBigInt(block.blockHash);
    if (!hashBigInt.lesserOrEquals(currentDifficulty)) {
        console.error('Invalid difficulty');
        return;
    }
    let result = blockchain.addBlock(block, false);
    result.block = block;
    return result;
};

app.use(bodyParser.urlencoded({ extended: false }));

app.use(function(req, res, next) {
    if(req.path === '/block') {
        let result = receiveBlock(req.body.block);
        res.sendStatus(200);
        if (result && !result.alreadyExists) {
            relayBlock(result.block, 0);
        }
    } else if (req.path.startsWith('/getblocks')) {
        let ancestor = req.url.split('?ancestor=')[1];
		ancestor = ancestor || BC.ROOT_HASH;
        let descendants = blockchain.getDescendants(ancestor);
        res.statusCode = 200;
        res.setHeader('Content-Type', 'text/plain');
        descendants.forEach((block) => {
            res.write(block.blockString() + '\n');
        });
        res.end();
    } else if (req.path.startsWith('/addpeer')) {
        if (!config.peers.includes(req.body.peer)) {
            config.peers.push(req.body.peer);
            console.log('peer ' + req.body.peer + ' connected');
        }
    } else {
        next();
    }
});

let relayBlock = (block, delay) => {
     setTimeout(() => {
        config.peers.forEach((peer) => {
            axios.post(peer + "/block", "block=" + encodeURIComponent(block.blockString()),
                {headers: {"Content-Type": "application/x-www-form-urlencoded"}})
                .catch((error) => {});
        });
     }, delay);
};

app.listen(config.port, () => {
    console.log("Server Started");
});

