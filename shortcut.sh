#!/bin/bash
mvn -e clean package appassembler:generate-daemons
chmod +x target/appassembler/jsw/appcompdupe/bin/*
mkdir target/appassembler/jsw/appcompdupe/logs
./appcompdupe console
