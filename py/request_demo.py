import requests
import urllib
import json
import pdb
solr_url = "http://localhost:8983/solr/"





def make_query(field_dict, start=0, rows=10, indent="on", wt="json"):
    q=""
    for k,v in field_dict.items():
        q += k + ":" + v
    q_dict = dict(
        q=q,
        start=start, rows=rows, indent=indent, wt=wt)

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

def parse_response(resp_string):
    ab = json.loads(resp_string)['response']
    return ab

def grab_docs(parsed_response):
    return parsed_response['docs']
    
def grab_first_doc(parsed_response):
    """ from a parsed response, this shows how to grab the first
    returned document """
    doc = parsed_response['docs'][0]
    print doc['title']
    print doc.keys()
    return doc


def doc_sections(doc):
    #note that the sections are also json_encoded, this was the most
    #expedient way of getting the data through solr

    return json.loads(doc['sectionParsed'])
def doc_section_titles(section_list):
    """ note, each section is a one item map of {title:[list of paragraphs]} """
    for section in section_list:
        print section.keys()[0]

def section_paragraphs(section):
    
    # values()[0] because values is the list of values of the map, not
    # the list, the list is values[0]
    for paragraph in section.values()[0]:
        print paragraph
        print "*"*10



def american_investigation():
    """ this function demonstrates playing with a search query for the term "american",
    
    it demonstrates how the object model of the response is traversed
    
    """
    american_response_obj = parse_response(make_request("american"))
   

    #first_doc = grab_first_doc(american_response_obj)
    doc = american_response_obj['docs'][4]

    print "-"*30, "title", "-"*30    
    print doc['title']


    print "-"*30, "section headings", "-"*30
    sections = doc_sections(doc)
    doc_section_titles(sections)


    print "-"*30, "section parsed json", "-"*30
    print doc['sectionParsed']

    print "-"*25, "section 2 paragraphs", "-"*25
    section_paragraphs(sections[2])
    #pdb.set_trace()




def url_to_response(original_url):
    """this shows how a wikipedia url is transformed into a request
    against the solr instance """

    # split original_url into strings, seperated by /, grab the last string
    end_of_url = original_url.split("/")[-1]

    article_title = end_of_url.replace("_", " ")
    
    #python printf style formatting
    wrapped_article_title = '"%s"' % article_title
    
    # we query the title field of solr for the exact article title
    poas_resp = make_request(q_obj=dict(title=wrapped_article_title))
    
    # since there should only be one doc matching this query, we
    # return the first one
    return parse_response(poas_resp)['docs'][0]



def url_query_investigation():
    url = "http://en.wikipedia.org/wiki/Politics_of_American_Samoa"
    poas_doc = url_to_response(url)

    print url, " maps to this document title ",  poas_doc['title']


def num_docs_in_response(resp_obj):
    return resp_obj['numFound']

def phrase_query_investigation():

    # note the nested quotes, we want to search for a phrase 

    q1 = '"american samoa"'
    q2 = '"samoa american"'
    _as = parse_response(make_request(q1))
    _sa = parse_response(make_request(q2))
    
    print "docs matching ",  q1,    num_docs_in_response(_as)
    print "docs matching ",  q2,    num_docs_in_response(_sa)

    # we can see that 'american samoa' occurs a lot more than 'samoa
    # american',  I think 'samoa american' shows up because there are
    # two consecutive sentences that look like 
    ''' The word \"American\" appears in the name of the U.S. territory of American Samoa. American Samoa began using the word American in its title when it became a U.S. territory." '''

    # The '.' between samoa and american is stripped, thus the phrase
    # 'samoa american' is possible 



    
print "#"*20 , "american investigation " ,  "#"*20
american_investigation()

print "#"*20 , "url query investigation " ,  "#"*20
url_query_investigation()

print "#"*20 , "phrase query investigation " ,  "#"*20
phrase_query_investigation()

