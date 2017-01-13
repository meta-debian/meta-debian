#
# debian-verify-version.bbclass
#
# Compare PV which is specified in recipe
# with upstream version which is parsed from debian/changelog

addtask debian_verify_version after do_unpack before do_debian_fix_timestamp
do_debian_verify_version[dirs] = "${DEBIAN_UNPACK_DIR}"
DEBIAN_CHANGELOG ?= "${DEBIAN_UNPACK_DIR}/debian/changelog"
do_debian_verify_version() {
	if [ ! -f ${DEBIAN_CHANGELOG} ]; then
		bbfatal "Could not find ${DEBIAN_CHANGELOG}."
	fi

	# Base on /usr/share/cdbs/1/rules/buildvars.mk, get Upstream version from debian/changelog
	DEB_VERSION=$(head -n 1 ${DEBIAN_CHANGELOG} | sed "s|.*(\([^()]*\)).*|\1|")
	DEB_NOEPOCH_VERSION=$(echo $DEB_VERSION | cut -d: -f2-)
	DEB_UPSTREAM_VERSION=$(echo $DEB_NOEPOCH_VERSION | sed 's/-[^-]*$//')

	if [ x"$DEB_UPSTREAM_VERSION" = x ]; then
		bbfatal "Could not parse source code version."
	elif [ "$DEB_UPSTREAM_VERSION" != "${PV}" ]; then
		bbwarn "${PN}: Source code version and PV mismatch. Source code version is $DEB_UPSTREAM_VERSION but PV is ${PV}"
	fi
}

EXPORT_FUNCTIONS do_debian_verify_version
