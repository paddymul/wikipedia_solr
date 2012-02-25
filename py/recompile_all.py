import os

os.system("cd ../lib/solr/solr/; ant clean compile example");
os.system("cd ../solr_home/Wikipedia_importer/wikipedia_solr ; ant clean compile jar");



