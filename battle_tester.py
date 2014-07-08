import time
import urllib2
import traceback
from multiprocessing import Pool

REQUESTS = 10000
PROCESSES = 20
URL1 = "http://localhost:8182/serve?app_url=http://go-lab.gw.utwente.nl/production/conceptmapper_v1/tools/conceptmap/src/main/webapp/conceptmapper.xml&lang=fr_ALL&target=ALL"
URL2 = "http://localhost:8182/serve?app_url=http://go-lab.gw.utwente.nl/THIS.DOES.NOT.EXIST.xml&lang=de_ALL&target=ALL"

def f((x, url, error_expected)):
    if x and x % 100 == 0:
        print x
    t0 = time.time()
    try:
        urllib2.urlopen(url).read()
    except urllib2.HTTPError:
        if not error_expected:
            print "ERROR! error NOT expected and happened!"
            traceback.print_exc()
    except:
        traceback.print_exc()
    else:
        if error_expected:
            print "ERROR! error expected and everything went fine!"
    finally:
        tf = time.time()
        return tf - t0

def test(msg, url, error_expected):
    print "Running %s requests %s in %s concurrent processes" % (REQUESTS, msg, PROCESSES)
    p = Pool(PROCESSES)
    t0 = time.time()
    results = p.map(f, [ (x, url, error_expected) for x in range(REQUESTS) ])
    tf = time.time()
    print "Finished in %.2f seconds" % (tf - t0)
    print "Average time:", 1.0 * sum(results) / REQUESTS
    print "Maximum time:", max(results) 

if __name__ == '__main__':
    test("of a resource that DOES EXIST", URL1, False)
    test("of a resource that DOES *NOT* EXIST", URL2, True)
