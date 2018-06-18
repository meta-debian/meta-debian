#
# debian-source.bbclass
#
# Parse .dsc file to detect and set SRC_URI for source codes
# which are located in Debian apt repository.

DEBIAN_SRC_URI ?= ""
SRC_URI = "${DEBIAN_SRC_URI}"

DEBIAN_UNPACK_DIR ?= "${WORKDIR}/${BPN}-${PV}"
S = "${DEBIAN_UNPACK_DIR}"

python __anonymous() {
    # Detect directory of package on Debian apt repo base on its name.
    # The path is like:
    #   http://ftp.debian.org/debian/pool/main/g/glibc
    def src_dir_detect():
        bpn = d.getVar('BPN', True)
        debian_mirror = d.getVar('DEBIAN_MIRROR', True)
        dir = 'main/'
        if bpn.startswith('lib'):
            dir += bpn[0:4] + '/' + bpn
        else:
            dir += bpn[0] + '/' + bpn
        return debian_mirror + '/' + dir


    # DEBIAN_SRC_URI in recipe has higher priority,
    # so, if it has been set, do nothing
    debian_src_uri = d.getVar('DEBIAN_SRC_URI', True)
    if debian_src_uri:
        return

    # Get .dsc file
    dsc_uri = d.getVar('DSC_URI', True)
    if not dsc_uri:
        return
    try:
        fetcher = bb.fetch2.Fetch([dsc_uri], d)
        fetcher.download()
    except bb.fetch2.BBFetchException as e:
        raise bb.build.FuncFailed(e)

    # Open .dsc file from downloads
    dl_dir = d.getVar('DL_DIR', True) or ""
    dsc_file = dsc_uri.split(";")[0].split("/")[-1]
    dsc_path = dl_dir + '/' + dsc_file
    src_dir = src_dir_detect()

    # Get list of files, md5sum and sha256sum
    with open(dsc_path, 'r') as dsc:
        files_detected = False
        sha256_detected = False
        for line in dsc:
            if line.startswith('Files:'):
                files_detected = True
            elif line.startswith('Checksums-Sha256:'):
                sha256_detected = True
            elif line.startswith(' '):
                if files_detected:
                    f = line.split()[2]
                    md5sum = line.split()[0]
                    debian_src_uri += "%s/%s;name=%s;apply=no " % (src_dir, f, f)
                    d.setVarFlag("SRC_URI", f + ".md5sum", md5sum)

                    # Dependency for unpack
                    if f.endswith('.xz') or f.endswith('.txz'):
                        d.appendVarFlag('do_unpack', 'depends', ' xz-native:do_populate_sysroot')

                elif sha256_detected:
                    f = line.split()[2]
                    sha256sum = line.split()[0]
                    d.setVarFlag("SRC_URI", f + ".sha256sum", sha256sum)
            else:
                files_detected = False
                sha256_detected = False

    d.setVar('DEBIAN_SRC_URI',debian_src_uri)
}

# Make folder "debian" be inside source code folder
addtask debian_unpack_extra after do_unpack before do_debian_verify_version
do_debian_unpack_extra() {
	if [ -d ${WORKDIR}/debian ]; then
		mv ${WORKDIR}/debian ${DEBIAN_UNPACK_DIR}/
	elif [ -f ${WORKDIR}/${BPN}_${PV}${DPR}.diff ]; then
		cd ${DEBIAN_UNPACK_DIR}
		patch -p1 < ${WORKDIR}/${BPN}_${PV}${DPR}.diff
	fi
}

EXPORT_FUNCTIONS do_debian_unpack_extra
