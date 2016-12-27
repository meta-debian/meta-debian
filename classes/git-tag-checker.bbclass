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

python base_do_unpack_append() {
    import subprocess

    git_tag = ""
    git_rebuild_tag = d.getVar("GIT_REBUILD_TAG", True) or ""
    git_preferred_tag = d.getVar("GIT_PREFERRED_TAG", True) or ""

    if git_rebuild_tag != "":
        git_tag = git_rebuild_tag
    elif git_preferred_tag != "":
        git_tag = git_preferred_tag

    if git_tag != "":
        for u in fetcher.urls:
            ud = fetcher.ud[u]
            if isinstance(ud.method, bb.fetch2.git.Git):
                os.chdir(ud.destdir)

                cmd = "git checkout -b tag-%s %s" % (git_tag, git_tag)
                try:
                    tag_hash = subprocess.check_output(cmd.split(), shell=False, stderr=subprocess.STDOUT).decode()
                    status = 0
                except subprocess.CalledProcessError as ex:
                    tag_hash = ex.output.decode()
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
}
