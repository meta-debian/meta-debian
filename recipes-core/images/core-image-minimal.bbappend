# forcibly remove ROOTFS_PKGMANAGE_BOOTSTRAP (run-postinsts)
# from the minimal package list
IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"
ROOTFS_PKGMANAGE_BOOTSTRAP = ""
