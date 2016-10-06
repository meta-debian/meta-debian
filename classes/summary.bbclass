#
# summary.bbclass
#
# Set INHERIT += "summary" to enable this class
#
# This class generates summary information about source codes of packages
# included in rootfs and SDK. As a first step, do_package_write_deb_prepend()
# checks required information and embed it to deb package as metadata.
# As a second step, gen_summary() generates summary information (csv) by
# parsing information embedded in packages included in rootfs/SDK.
#

# if "1" add value of LIC_FILES_CHKSUM to summary, otherwise don't
SUMMARY_LIC_FILES_CHKSUM ?= ""

python do_package_deb_prepend() {
    import os.path
    import subprocess
    import re

    add_licfiles = d.getVar("SUMMARY_LIC_FILES_CHKSUM", True) == "1"

    dpn = d.getVar("DPN", True) or ""

    # Debian package (PN inherits debian-package.bbclass)
    if dpn is not "":
        # DebianSourceName
        deb_src_name = dpn

        # DebianSourceVersion
        # get source package version from the first line of debian/changelog
        unpack_dir = d.getVar("DEBIAN_UNPACK_DIR", True) or ""
        if unpack_dir is "":
            bb.fatal("%s: DEBIAN_UNPACK_DIR is empty" % dpn)
        changelog = unpack_dir + "/debian/changelog"
        if os.path.isfile(changelog):
            cmd = "head -1 %s" % changelog
            try:
                head = subprocess.check_output(cmd.split(), shell=False, stderr=subprocess.STDOUT).decode()
                status = 0
            except subprocess.CalledProcessError as ex:
                head = ex.output.decode()
                status = ex.returncode
            if status != 0:
                bb.fatal("%s: failed to read %s" % (dpn, changelog))
            search = re.compile("\(.*\)").search(head)
            if search is None:
                bb.fatal("%s: failed to parse %s" % (dpn, changelog))
            deb_src_ver = re.compile("[\(\)]").sub("", search.group())
        else:
            bb.fatal("%s: %s not found" % (dpn, changelog))

        # RemoteSourceURI (Debian package)
        remote_src_uri = d.getVar("DEBIAN_SRC_URI", True) or ""
        if remote_src_uri is "":
            bb.fatal("%s: DEBIAN_SRC_URI is empty" % dpn)
    # non-Debian package
    else:
        # RemoteSourceURI (non-Debian package)
        src_uri = d.getVar("SRC_URI", True)
        remote_uris = []
        for uri in src_uri.split():
            ud = bb.fetch2.FetchData(uri, d)
            if isinstance(ud.method, bb.fetch2.local.Local):
                continue
            remote_uris.append(uri)
        remote_src_uri = " ".join(remote_uris)

    if add_licfiles:
        # LicenseFiles
        licfiles = d.getVar("LIC_FILES_CHKSUM", True) or ""
        if licfiles is "":
            bb.fatal("LIC_FILES_CHKSUM is empty")

    for pkg in d.getVar("PACKAGES", True).split():
        # License
        license = d.getVar("LICENSE_" + pkg, True) or ""
        if license is "":
            license = d.getVar("LICENSE", True)

        # embed metadata
        metadata = ""
        if dpn is not "":
            metadata = metadata + "DebianSourceName: " + deb_src_name + "\n"
            metadata = metadata + "DebianSourceVersion: " + deb_src_ver + "\n"
        metadata = metadata + "RemoteSourceURI: " + remote_src_uri + "\n"
        metadata = metadata + "License: " + license + "\n"
        if add_licfiles:
            metadata = metadata + "LicenseFiles: " + licfiles + "\n"
        d.setVar("PACKAGE_ADD_METADATA_DEB_" + pkg, metadata)
}

def gen_summary(d, tree, csv):
    import os.path
    import re

    # put the value of these fields into csv
    fields = [ \
                "Package", \
                "Version", \
                "OE", \
                "DebianSourceName", \
                "DebianSourceVersion", \
                "RemoteSourceURI", \
                "License"
    ]
    if d.getVar("SUMMARY_LIC_FILES_CHKSUM", True) == "1":
        fields.append("LicenseFiles")

    # field names are put according to these mappings
    fields_map = { \
                "Package":"PackageName", \
                "Version":"PackageVersion", \
                "OE":"RecipeName"
    }

    # status of all packages installed in the tree
    status = os.path.join(tree, "var/lib/dpkg/status")
    if not os.path.isfile(status):
        bb.fatal("gen_summary: %s not found" % status)

    # parse status file and store required field info into "csv_info"
    f = open(status)
    lines = f.readlines()
    csv_info = []
    info = {}
    for line in lines:
        line = line.replace("\n", "")
        # empty line means "terminator"
        # "info" must have enough field info already
        if len(line) is 0:
            if len(info) is 0:
                bb.fatal("gen_summary: failed to parse %s" % status)
            csv_info.append(info)
            info = {}
            continue
        for field in fields:
            if re.match("^%s: " % field, line):
                info[field] = line.replace("%s: " % field, "")
    f.close()

    # generate csv
    f = open(csv, "w")
    for field in fields:
        if field in fields_map:
            f.write("%s," % fields_map[field])
        else:
            f.write("%s," % field)
    f.write("\n")
    for info in sorted(csv_info, key=lambda info: info["Package"]):
        for field in fields:
            if field in info:
                f.write("%s," % info[field])
            else:
                f.write("N/A,")
        f.write("\n")
    f.close()

python gen_rootfs_summary() {
    tree = d.getVar("IMAGE_ROOTFS", True)
    csv = os.path.join(d.getVar("WORKDIR", True), "rootfs-summary.csv")
    gen_summary(d, tree, csv)
}

python gen_sdk_target_summary() {
    sdk_output = d.getVar("SDK_OUTPUT", True)
    sdk_path_target = d.getVar('SDKTARGETSYSROOT', True).strip("/")
    tree = os.path.join(sdk_output, sdk_path_target)
    csv = os.path.join(d.getVar("WORKDIR", True), "sdk-target-summary.csv")
    gen_summary(d, tree, csv)
}

python gen_sdk_host_summary() {
    tree = d.getVar("SDK_OUTPUT", True)
    csv = os.path.join(d.getVar("WORKDIR", True), "sdk-host-summary.csv")
    gen_summary(d, tree, csv)
}

ROOTFS_POSTPROCESS_COMMAND += "gen_rootfs_summary ; "
POPULATE_SDK_POST_TARGET_COMMAND += "gen_sdk_target_summary ; "
POPULATE_SDK_POST_HOST_COMMAND += "gen_sdk_host_summary ; "
