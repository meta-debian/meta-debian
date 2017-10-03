#
# git-tracer.bbclass
#
# Set INHERIT += "git-tracer" to enable this class
#

#
# git_tag_checker()
#
# This feature can help you to build git repository sources by specified tag.
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

def git_tag_checker(d, ud):
    import subprocess

    git_tag = ""
    git_rebuild_tag = d.getVar("GIT_REBUILD_TAG", True) or ""
    git_preferred_tag = d.getVar("GIT_PREFERRED_TAG", True) or ""

    if git_rebuild_tag != "":
        git_tag = git_rebuild_tag
    elif git_preferred_tag != "":
        git_tag = git_preferred_tag

    if git_tag != "":
        cmd = "git checkout -b tag-%s %s" % (git_tag, git_tag)
        try:
            out_tmp = subprocess.check_output(cmd.split(), shell=False, stderr=subprocess.STDOUT).decode()
            status = 0
        except subprocess.CalledProcessError as ex:
            out_tmp = ex.output.decode()
            status = ex.returncode

        if git_rebuild_tag != "":
            if status != 0:
                bb.fatal("Failed to checkout GIT_REBUILD_TAG(%s) in %s" % (git_tag, ud.destdir))
            else:
                bb.note("Success to checkout GIT_REBUILD_TAG(%s) in %s" % (git_tag, ud.destdir))
        elif git_preferred_tag != "":
            if status != 0:
                bb.note("There is no GIT_PREFERRED_TAG(%s) in %s" % (git_tag, ud.destdir))
            else:
                bb.note("Success to checkout GIT_PREFERRED_TAG(%s) in %s" % (git_tag, ud.destdir))

#
# git_tracer()
#
# This feature can help you to trace git repositories.
# You can see the list of git repositories by ${TMPEDIR}/git.list.<server>.
# It contain path of repository and hash number.
#

GIT_LIST_BASE = "${TMPDIR}/git.list"

def git_tracer(d, ud):
    import subprocess
    import os

    cmd = "git rev-parse HEAD"
    try:
        git_commitid = subprocess.check_output(cmd.split(), shell=False, stderr=subprocess.STDOUT).decode()
        status = 0
    except subprocess.CalledProcessError as ex:
        git_commitid = ex.output.decode()
        status = ex.returncode

    if status != 0:
        bb.fatal("Failed to get hash value from repository")

    # Get parameters
    git_list_base = d.getVar("GIT_LIST_BASE", True)

    # Outpu lists
    f = open("%s.%s" % (git_list_base, ud.host), 'a')
    f.write("%s %s" % (ud.path[1:], git_commitid))
    f.close()

python base_do_unpack_append() {
    # fetcher is built in base_do_unpack
    for u in fetcher.urls:
        ud = fetcher.ud[u]

        # target is git repository
        if isinstance(ud.method, bb.fetch2.git.Git):
            os.chdir(ud.destdir)

            git_tag_checker(d, ud)
            git_tracer(d, ud)
}
