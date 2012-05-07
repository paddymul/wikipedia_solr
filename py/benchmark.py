import requests
import urllib
import json
import pdb
import argparse
import time
solr_url = "http://localhost:8983/solr/"





def make_query(field_dict, start=0, rows=10, indent="on", wt="json"):
    q=""
    for k,v in field_dict.items():
        q += k + ":" + v
    q_dict = dict(
        q=q,
        start=start, rows=rows, indent=indent, wt=wt)

    return urllib.urlencode(q_dict)


def make_query2(q_string,  **kwargs):
    if hasattr(q_string, "to_query"):
        q_string = q_string.to_query()
    q=""

    q_dict = dict(q=q_string, start=0, rows=10, indent="on", wt="json")
    q_dict.update(kwargs)

    return urllib.urlencode(q_dict)


def make_request(query=False, q_obj=False):

    if query:
        url = solr_url+ "select/?" + make_query({"articlePlainText":query})
    elif q_obj:
        url = solr_url+ "select/?" + make_query(q_obj)
    else:
        Exception("neither query nor q_obj provided, can't make a request")
    #print url
    r = requests.get(url)
    ab = r.text
    bc = ab.encode("utf-8")
    return bc



class Phrase(object):
    def __init__(self, phrase, field="articlePlainText"):
        self.phrase = phrase
        self.field = field
    
    def to_query(self):
        return '''_query_:"%s:\\"%s\\""''' % (self.field, self.phrase)


class And(object):
    def __init__(self, *queries):
        self.queries = queries
    
    def to_query(self):
        sub_queries_as_strings = [q.to_query() for q in self.queries]
        return " AND ".join(sub_queries_as_strings)

class Or(object):
    def __init__(self, *queries):
        self.queries = queries
    
    def to_query(self):
        sub_queries_as_strings = [q.to_query() for q in self.queries]
        return " OR ".join(sub_queries_as_strings)

def parse_response(resp_string):
    ab = json.loads(resp_string)['response']
    return ab




def q_response(q_string):
    url = solr_url+ "select/?" + make_query2(q_string)
    #print "url     ", url
    r = requests.get(url)
    ab = r.text
    bc = ab.encode("utf-8")
    parsed = json.loads(bc)

    #print "q_string ", q_string
    #print "QTime    ", parsed['responseHeader']['QTime']
    #print "params   ", parsed['responseHeader']['params']
    #print "numFound ", parsed['response']['numFound']
    return parsed

def query_num(q_string):
    return q_response(q_string)['response']['numFound']

def query_speed(q_string):
    return q_response(q_string)['responseHeader']['QTime']

import time

def wait_for_doc_num(doc_num, max_step=60, max_timeout=60*60*24):
    timeout_step = 1

    while query_num("*:*") < doc_num and max_timeout > 0:
        time.sleep(timeout_step)
        max_timeout -= timeout_step
        timeout_step *= 2
        timeout_step = min(timeout_step, max_step)
        print "waiting %d secs " % timeout_step

    return max_timeout > 0


queries = dict(
    single_word=Phrase("anarchy"),
    double_word=Phrase("abraham lincoln"),
    and_query=And(Phrase("chicago"), Phrase("tribune")),
    or_query=Or(Phrase("democratic"), Phrase("platform")),
    double_word_and=And(Phrase("seven fingers"), Phrase("practical devices")),
    double_word_or=Or(Phrase("canadian english"), Phrase("united states")))

query_order = ["single_word", "double_word", "and_query", "or_query",
               "double_word_and", "double_word_or"]






if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Create some testcases.')
    parser.add_argument(
        '--output_file', help='where should results be appended to')
    parser.add_argument(
        '--target_count', help='how many documents should be in the index before startign ')


    args = parser.parse_args()

    
    wait_for_doc_num(int(args.target_count))
    
    q_times = [query_speed(queries[x]) for x in query_order]
    print ",".join(query_order)
    print ",".join(map(str,q_times))
    
    open(args.output_file, "a").write(",".join(map(str,q_times)) + '\n')
    



