import argparse
import requests
from query_demo import make_query2, query_play, Phrase
import json

#def run_query(q_string, solr_url="http://thsh.servehttp.com:8983",  **kwargs):
def run_query(q_string, solr_url="http://localhost:8983",  **kwargs):
    url = solr_url+ "/solr/select/?" + make_query2(q_string, **kwargs)
    #print "url     ", url

    r = requests.get(url)
    ab = r.text
    bc = ab.encode("utf-8")
    return json.loads(bc)

def first(result_set):
    print result_set['response']['docs'][0]['wikimediaMarkup'].encode("utf-8")

def id_(result_set):
    print result_set['response']['docs'][0]['id']





if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Create some testcases.')
    parser.add_argument('--document_id', help='document_id for the offending article')
    parser.add_argument('--title', help='title for the offending article')
    

    parser.add_argument('--sum', dest='accumulate', action='store_const',
                        const=sum, default=max,
                        help='sum the integers (default: find the max)')
    args = parser.parse_args()
    if args.document_id:
        first(run_query(Phrase(args.document_id, "id").to_query()))
    else: 
        rs = run_query(Phrase(args.title, "title").to_query())
        id_(rs)
        first(rs)



