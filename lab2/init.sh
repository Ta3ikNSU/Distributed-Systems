#!/bin/bash

mongo <<EOF
var config = {
    "_id": "rs0",
    "version": 1,
    "members": [
        {
            "_id": 1,
            "host": "mongo:27017",
            "priority": 3
        },
        {
            "_id": 2,
            "host": "mongodb_secondary_1:27017",
            "priority": 2
        },
        {
            "_id": 3,
            "host": "mongodb_secondary_2:27017",
            "priority": 1
        }
    ]
};
rs.initiate(config, { force: true });
rs.status();
EOF