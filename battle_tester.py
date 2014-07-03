import time
import urllib2
import traceback
from multiprocessing import Pool

REQUESTS = 10000
PROCESSES = 20
URL = "http://localhost:8182/serve?app_url=http://go-lab.gw.utwente.nl/production/conceptmapper_v1/tools/conceptmap/src/main/webapp/conceptmapper.xml&lang=de_ALL&target=ALL"

def f(x):
    if x and x % 100 == 0:
        print x
    t0 = time.time()
    try:
        urllib2.urlopen(URL).read()
    except urllib2.HTTPError:
        pass
    except:
        traceback.print_exc()
    finally:
        tf = time.time()
        return tf - t0

if __name__ == '__main__':
    print "Running %s requests in %s concurrent processes" % (REQUESTS, PROCESSES)
    p = Pool(PROCESSES)
    t0 = time.time()
    results = p.map(f, range(REQUESTS))
    tf = time.time()
    print "Finished in %.2f seconds" % (tf - t0)
    print "Average time:", 1.0 * sum(results) / REQUESTS
    print "Maximum time:", max(results) 
