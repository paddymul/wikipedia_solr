import os

os.system("cd ../lib/solr/solr/example/; java   -Xms512M -Xmx1024M  -Dsolr.solr.home=../../../../solr_home/solr -Dsolr.absolutePath=`pwd`/../../../../solr_home/solr  -jar start.jar")


