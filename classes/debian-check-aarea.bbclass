#
# debian-check-aarea.bbclass
#
# Check archive area (main, contrib, non-free) of source package
# then raise an error if the archive area is not "main" and
# not included in DEBIAN_ALLOWED_ARCHIVE_AREA. This is
# intended to avoid installing non-free packages unintentionally.
#

# Users need to expressly add non-main archive areas
# (contrib, non-free) to this variable in local.conf etc.
DEBIAN_ALLOWED_ARCHIVE_AREA ?= ""

DEBIAN_CONTROL ?= "${DEBIAN_UNPACK_DIR}/debian/control"

addtask debian_check_aarea after do_debian_verify_version before do_debian_fix_timestamp
do_debian_check_aarea[dirs] = "${DEBIAN_UNPACK_DIR}"
do_debian_check_aarea() {
	if [ ! -f ${DEBIAN_CONTROL} ]; then
		bbfatal "Could not find ${DEBIAN_CONTROL}"
	fi

	# Packages in "main" archive area: "Section: section"
	# Packages in other archive areas: "Section: area/section"
	non_main=$(grep "^Section: .*/" ${DEBIAN_CONTROL} | head -1 | \
	           sed "s@.*: \([^/]*\)/.*@\1@")

	if [ -z "${non_main}" ]; then
		bbnote "Archive area: main"
		return 0
	fi
	for allowed in ${DEBIAN_ALLOWED_ARCHIVE_AREA}; do
		if [ "${non_main}" = "${allowed}" ]; then
			bbnote "Archive area: ${non_main} (in DEBIAN_ALLOWED_ARCHIVE_AREA)"
			return 0
		fi
	done
	bbfatal "Archive area \"${non_main}\" is not in DEBIAN_ALLOWED_ARCHIVE_AREA"
}

EXPORT_FUNCTIONS do_debian_check_aarea
