#!/bin/bash

export BONSAI=/Users/lorrie/Desktop/workspace/IHome/bonsai/
$BONSAI/bin/bonsai_bky_parse_via_clust.sh -f ldep $BONSAI/query.txt > $BONSAI/dependencies.txt
