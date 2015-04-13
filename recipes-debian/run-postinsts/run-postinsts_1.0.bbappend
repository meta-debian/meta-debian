#FILESEXTRAPATHS_prepend = "${THISDIR}/files:"
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta-debian/recipes-debian/run-postinsts/files:\
"

# run-postinsts.init from base recipe missing LSB
# tag. run-postinsts_debian.init was created with
# same purpose but included LSB tag.
SRC_URI += "\
	file://run-postinsts_debian.init \
"

do_install_append () {
	install -m 0755 ${WORKDIR}/run-postinsts_debian.init \
			${D}${sysconfdir}/init.d/run-postinsts
}
