# forcibly remove ROOTFS_PKGMANAGE_BOOTSTRAP (run-postinsts)
# from the minimal package list
IMAGE_INSTALL = "packagegroup-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"
ROOTFS_PKGMANAGE_BOOTSTRAP = ""

# update-rc.d is no longer supported in our system, so
# remove dependency on update-rc.d-native task from do_rootfs.
# do_rootfs[depends] is defined in image.bbclass, therefore
# need to remove the dependency by this tricky code.
python () {
    removed_task = "update-rc.d-native:do_populate_sysroot"
    deps = d.getVarFlag("do_rootfs", "depends", False)
    d.setVarFlag("do_rootfs", "depends", deps.replace(removed_task, ""))
}
