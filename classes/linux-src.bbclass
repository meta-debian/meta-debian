#
# linux-src.bbclass
#
# This class file is shared by linux-base_git.bb and
# linux-libc-headers-base_git.bb. Please define the following
# controllable variables in a global configuration file.
#
# LINUX_GIT_URI: 'dirname' of the repository URI (must begin with git://)
# LINUX_GIT_PROTOCOL: protocol (git, http, https, etc.)
# LINUX_GIT_PREFIX: prefix for LINUX_GIT_REPO
# LINUX_GIT_REPO: 'basename' of the repository URI
# LINUX_GIT_SRCREV: a branch name or a commit hash
#
# Example:
#   LINUX_GIT_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/stable"
#   LINUX_GIT_PROTOCOL = "https"
#   LINUX_GIT_PREFIX = ""
#   LINUX_GIT_REPO = "linux-stable.git"
#   LINUX_GIT_SRCREV = "linux-3.10.y"
#

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

SRC_URI = "${LINUX_GIT_URI}/${LINUX_GIT_PREFIX}${LINUX_GIT_REPO};branch=${SRCREV};protocol=${LINUX_GIT_PROTOCOL}"

SRCREV = "${LINUX_GIT_SRCREV}"
PV = "git${SRCPV}"

S = "${WORKDIR}/git"
