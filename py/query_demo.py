import requests
import urllib
import json
import pdb
solr_url = "http://localhost:8983/solr/"


def make_query2(q_string, start=0, rows=10, indent="on", wt="json"):
    q=""

    q_dict = dict(
        q=q_string,
        start=start, rows=rows, indent=indent, wt=wt)

    return urllib.urlencode(q_dict)

def parse_response(resp_string):
    ab = json.loads(resp_string)['response']
    return ab

def query_play(q_string):
    url = solr_url+ "select/?" + make_query2(q_string)
    print "url     ", url
    r = requests.get(url)
    ab = r.text
    bc = ab.encode("utf-8")
    parsed = json.loads(bc)
    print "q_string ", q_string
    print "QTime    ", parsed['responseHeader']['QTime']
    print "params   ", parsed['responseHeader']['params']
    print "numFound ", parsed['response']['numFound']
    return parsed['response']['numFound']


qp = query_play
american = qp('''articlePlainText:"american"''')

american_no_quote = qp('''articlePlainText:american''')

assert american == american_no_quote

samoa = qp('''articlePlainText:samoa''')


american_or_samoa = qp(
    '''articlePlainText:american OR _query_:"articlePlainText:samoa"''')

american_and_samoa = qp(
    '''articlePlainText:american AND _query_:"articlePlainText:samoa"''')

samoa_not_american =qp(
    '''articlePlainText:samoa NOT _query_:"articlePlainText:american"''')

american_samoa_phrase = qp('''articlePlainText:"american samoa"''')

assert american_or_samoa == (american + samoa_not_american)

assert american_and_samoa >= american_samoa_phrase


double_phrase_query = qp(
    '''articlePlainText:"american samoa" AND _query_:"articlePlainText:'manifest destiny'"''')

