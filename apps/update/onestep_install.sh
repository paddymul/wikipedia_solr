#!/bin/bash
git clone git://github.com/paddymul/wikipedia_solr.git
cd wikipedia_solr
echo "######## lines after this added by wikipedia_solr ######"  >> ~/.profile
echo "export GC_WIKIPEDIA_REPO_ROOT=\"`pwd`\" " >> ~/.profile
echo "export GC_WIKIPEDIA_DL_DIR=\"`pwd`/dl_dir\" " >> ~/.profile
echo "export GC_WIKIPEDIA_SOLR_DATA_DIR=\"`pwd`/solr_data_dir\" " >> ~/.profile
./apps/update/update_all
./apps/update/download_dump
./apps/start_solr/start_solr &
./apps/reindex/index_1_000

curl "http://localhost:8983/solr/select/?q=articlePlainText%3A%22american%22&start=0&rows=10&indent=on&wt=json"

kill %1


