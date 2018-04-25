#!/bin/bash
./appcompdupe stop
mvn -e clean package appassembler:generate-daemons
chmod +x target/appassembler/jsw/appcompdupe/bin/*
mkdir target/appassembler/jsw/appcompdupe/logs
./appcompdupe start
./appcompdupe console
tail -f target/appassembler/jsw/appcompdupe/wrapper.log
