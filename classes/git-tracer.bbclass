#
# git-tracer.bbclass
#
# Set INHERIT += "git-tracer" to enable this class
#
# This class can help to trace git repositories.
# You can see the list of git repositories by ${TMPEDIR}/git.list.<server>.
# It contain path of repository and hash number.
#  

GIT_LIST_BASE = "${TMPDIR}/git.list"

base_do_fetch_append() {
    import re
    import os

    # fetcher is built in base_do_fetch
    for u in fetcher.urls:
        ud = fetcher.ud[u]

        # target is git repository
        if isinstance(ud.method, bb.fetch2.git.Git):

            # Get parameters
            git_commitid = ud.method._build_revision(ud, u, ud.names[0])
            git_list_base = d.getVar("GIT_LIST_BASE", True)

            # Outpu lists
            f = open("%s.%s" % (git_list_base, ud.host), 'a')
            f.write("%s %s\n" % (ud.path[1:], git_commitid))
            f.close()
}
