#
# git-tag-checker.bbclass
#
# Set INHERIT += "git-tag-checker" to enable this class
#
# This class can help to build git repository sources by specified tag.
# When building, git repository source be chekouted by specified tag.
# You can specify tag by GIT_REBUILD_TAG or GIT_PREFERRED_TAG.
#
# GIT_REBUILD_TAG:
#  Specified tag will be used for all repositories. All repositories which 
#  used for building need to contain specified tag. Otherwise, build will
#  be failed.
#
# GIT_PREFERRED_TAG:
#  Specified tag will be used if repository contains the tag. If repository
#  doesn't contain specified tag, it won't be set. GIT_PREFERRED_TAG are 
#  completely ignored if GIT_REBUILD_TAG is defined.
#

SRCPV = ""
python __anonymous () {
    import re
    import commands

    git_tag = ""
    git_rebuild_tag = d.getVar("GIT_REBUILD_TAG", True) or ""
    git_preferred_tag = d.getVar("GIT_PREFERRED_TAG", True) or ""

    if git_rebuild_tag != "":
        git_tag = git_rebuild_tag
    elif git_preferred_tag != "":
        git_tag = git_preferred_tag

    if git_tag != "":
        scms = 0
        src_uri_new = []

        # Get original SRC_URI
        src_uri = d.getVar('SRC_URI', True).split()

        # Get fetcher instance
        fetcher = bb.fetch2.Fetch(src_uri, d, False)

        # Check each URLs
        for u in fetcher.urls:
            ud = fetcher.ud[u]
            git_specified_tag = ""

            # check scms
            if ud.method.supports_srcrev():
                scms += 1

            # Set git_tag if specified GIT_REBUILD_TAG or GIT_PREFERRED_TAG
            if isinstance(ud.method, bb.fetch2.git.Git):
                repourl = ud.method._get_repo_url(ud)
                cmd = "%s ls-remote --exit-code %s %s" % (ud.basecmd, repourl, git_tag)

                status, output = commands.getstatusoutput(cmd)
                if status == 0:
                    git_specified_tag = git_tag

            # Set git_tag to URI
            if git_specified_tag != "":
                #u = re.sub(r';branch=[^;]*', '', u)
                u += ";tag=%s" % git_specified_tag
                d.delVar("SRCREV")

            src_uri_new.append(u)

        # Set new SRC_URI
        d.setVar("SRC_URI", " ".join(src_uri_new))

        # Set SRCPV if there is some scms
        if scms != 0:
            srcpv = bb.fetch2.get_srcrev(d)
            d.setVar("SRCPV", srcpv)
}

python base_do_unpack_append() {
    import commands

    git_rebuild_tag = d.getVar("GIT_REBUILD_TAG", True) or ""
    if git_rebuild_tag != "":
        for u in fetcher.urls:
            ud = fetcher.ud[u]
            if isinstance(ud.method, bb.fetch2.git.Git):
                os.chdir(ud.destdir)

                cmd = "git rev-parse %s" % git_rebuild_tag
                status, tag_hash = commands.getstatusoutput(cmd)
                if status != 0:
                    bb.fatal("%s doesn't contain GIT_REBUILD_TAG(%s)" % (ud.destdir, git_rebuild_tag))

                cmd = "git rev-parse HEAD"
                status, head_hash = commands.getstatusoutput(cmd)
                if status != 0:
                    bb.fatal("%s doesn't contain HEAD" % ud.destdir)

                if tag_hash != head_hash:
                    bb.fatal("GIT_REBUILD_TAG(%s) doesn't matched with source HEAD" % git_rebuild_tag)
}
