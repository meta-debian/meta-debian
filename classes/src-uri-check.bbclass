#
# src-uri-check.bbclass
#
# Set INHERIT += "src-uri-check" to enable this class
#
# This class confirms that all sources in SRC_URI except local files
# are fetched from one of allowed URIs (SRC_URI_ALLOWED).
#
# URIs that don't match with SRC_URI_ALLOWED by 'prefix search'
# are printed as WARNING by default. If SRC_URI_CHECK_ERROR is 1,
# build stops with ERROR when such URIs are found.
# Do nothing if SRC_URI_ALLOWED is not set.
#

# if "1" raise ERROR, otherwise just print WARNING
SRC_URI_CHECK_ERROR ?= ""

def check_src_uri(fetcher, d):
    import re

    allowed = d.getVar("SRC_URI_ALLOWED", True) or ""
    if allowed is "":
        return

    for u in fetcher.urls:
        # ignore local files
        if isinstance(fetcher.ud[u].method, bb.fetch2.local.Local):
            continue
        uri = "%s: SRC_URI %s" % (d.getVar("PN", True) or "", u)
        for a in allowed.split():
            if re.compile("^" + a).match(u):
                bb.note("%s matches %s in SRC_URI_ALLOWED" % (uri, a))
                return
        if (d.getVar("SRC_URI_CHECK_ERROR", True) or "") is "1":
            bb.fatal("%s doesn't match SRC_URI_ALLOWED" % uri)
        else:
            bb.warn("%s doesn't match SRC_URI_ALLOWED" % uri)

base_do_fetch_append() {
    # fetcher is built in base_do_fetch
    check_src_uri(fetcher, d)
}
