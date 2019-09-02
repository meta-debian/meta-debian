FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "${LINUX_GIT_URI}/${LINUX_GIT_PREFIX}${LINUX_GIT_REPO};branch=${LINUX_GIT_BRANCH};protocol=${LINUX_GIT_PROTOCOL} \
          file://base.config"

SRC_URI_append_qemuall += "file://qemu-emlinux.config"
