# debian-package-ng.bbclass
# Get source code from snapshot.debian.org
#
# Copyright: Nobuhiro Iwamatsu <iwamatsu@nigauri.org>
#            Nobuhiro Iwamatsu <nobuhiro.iwamatsu@miraclelinux.com>

DPN ?= "${BPN}"
def get_debian_src_uris (d, pkgname, pkgver):
    import json, os

    def _readjsonstr(path):
        import urllib.request

        base_uri = "http://snapshot.debian.org"
        try:
            readobj = urllib.request.urlopen(base_uri + path)
        except urllib.error.URLError as e:
            bb.fatal ('Can not access to %s' % base_uri + path)
            print(e.reason)
        else:
            return readobj.read()

    def _managejsonfile (api_path, json_path):
        if os.path.exists(json_path):
            with open(json_path) as f:
                return f.read()
        else:
            _data = _readjsonstr(api_path)
            if _data is not None:
                with open(json_path, mode = 'w') as f:
                    data = _data.decode('utf-8')
                    f.write(data)

                    return data

    def _srcfiles (dl_dir, pkgname, pkgver):
        api_path = os.path.join('/mr/package/', pkgname, pkgver, 'srcfiles')
        json_path = os.path.join(dl_dir, pkgname + '_' + pkgver + '_srcfiles.json')

        return _managejsonfile(api_path, json_path)

    def _fileinfo (dl_dir, filehash, pkgname, pkgver):
        api_path = os.path.join('/mr/file/', filehash, 'info')
        json_path = os.path.join(dl_dir, pkgname + '_' + pkgver + '_' + filehash +'_info.json')

        return _managejsonfile(api_path, json_path)

    def _createfilepath (pkgname, fileinfo):
        # jsondata = json.loads(fileinfo.decode(encoding='utf-8'))
        jsondata = json.loads(fileinfo)

        # result data check
        results = jsondata['result']
        if results is None:
            return

        for i in range(len(results)):
            filename = results[i]['name']
            archive = results[i]['archive_name']
            p = filename.split('_')
            if p[0] in pkgname :
                archive_path = results[i]['path']
                first_seen = results[i]['first_seen']

                return filename, archive_path, first_seen, archive
    
    dl_dir = d.getVar('DL_DIR', True)

    # check and create DL_DIR
    if os.path.exists(dl_dir) is not True:
        os.makedirs(dl_dir)

    srcfiles = _srcfiles(dl_dir, pkgname, pkgver)
    if srcfiles is None:
        return

    jsondata = json.loads(srcfiles)
    # jsondata = json.loads(srcfiles.decode(encoding='utf-8'))

    # version check
    if jsondata['version'] not in pkgver:
        return

    # result data check
    results = jsondata['result']
    if results is None:
        return

    debfile_uris = ''
    dscfile = ''
    for i in range(len(results)):
        filehash = results[i]['hash']
        fileinfo = _fileinfo(dl_dir, filehash, pkgname, pkgver)
        if fileinfo is None:
            continue

        info = _createfilepath(pkgname, fileinfo)
        if info is None:
            continue
        u = "http://snapshot.debian.org/archive/" + \
                info[3] + "/" + \
                info[2] + \
                info[1] + "/" + \
                info[0]
        # dsc
        if '.dsc' in os.path.splitext(info[0])[1]:
            dscfile = u + ";name=dsc" + " "
            u = ''
            bb.debug(2, 'URI(dsc): %s' % dscfile)
        # debian specific data
        elif '.debian.tar.' in info[0]:
            u = u + ";name=debian"
            bb.debug(2, 'URI(debian): %s' % u)
        # old source format
        elif '.diff.' in info[0]:
            u = u + ";name=patch"
            bb.debug(2, 'URI(diff): %s' % u)
        # tar
        else:
            u = u + ";name=tarball"
            bb.debug(2, 'URI(tarball): %s' % u)

        debfile_uris = debfile_uris + ' ' + u
        bb.debug(2, 'URI update: %s' % debfile_uris)

    debfile_uris = dscfile + debfile_uris

    if not debfile_uris:
        bb.bbfatal('Can not get URI of debian source packages.')
        return None

    bb.debug(2, 'URI(finish): %s' % debfile_uris)

    return debfile_uris

def debian_src_uri(d):
    pn = d.getVar("DPN", True)

    pv = d.getVar("DPV", True)
    if pv is None:
        pv = d.getVar("PV", True)

    return get_debian_src_uris (d, pn, pv)

def debian_src_version(d):
    pv = d.getVar("PV", True)

    # split version of source and debian
    return pv.split('-')[0]

DEB_SRC_URI ?= "${@debian_src_uri(d)}"
SRC_URI += "${DEB_SRC_URI}"
DEB_SRC_VERSION ?= "${@debian_src_version(d)}"

S = "${WORKDIR}/${DPN}-${DEB_SRC_VERSION}"

python do_fetch_prepend () {
    import re

    src_uri = (d.getVar('SRC_URI', True) or "").split()
    if len(src_uri) == 0:
        return

    try:
        fetcher = bb.fetch2.Fetch([src_uri[0]], d)
        fetcher.download()
    except bb.fetch2.BBFetchException as e:
        raise bb.build.FuncFailed(e)
        return

    sha256_idx = 1
    md5_idx = 1

    # re-initialize fetcher
    try:
        fetcher = bb.fetch2.Fetch(src_uri, d)
    except bb.fetch2.BBFetchException as e:
        raise bb.build.FuncFailed(e)
        return

    try:
        with open(fetcher.localpath(src_uri[0])) as f:
            data = f.read()
            for m in re.finditer(r'^ ([0-9a-f]*) ([0-9]*) (.*)$', data, re.MULTILINE):
                # sha256sum
                if len(m.group(1)) == 64:
                    fetcher.ud[src_uri[sha256_idx]].sha256_expected = m.group(1)
                    sha256_idx += 1
                # md5sum
                if len(m.group(1)) == 32:
                    fetcher.ud[src_uri[md5_idx]].md5_expected = m.group(1)
                    md5_idx += 1

    except Exception as e:
        raise bb.build.FuncFailed(e)
        return
}

python do_unpack_append() {
    import os.path, shutil
    
    workdir = d.getVar('WORKDIR', True)
    srcdir = d.getVar('S', True)
    srcdir_nover = os.path.join(workdir, d.getVar("DPN", True))
    debiandir = os.path.join(workdir, 'debian')

    if os.path.exists(srcdir_nover):
        shutil.rmtree(srcdir)
        shutil.move(srcdir_nover, srcdir)

    if os.path.exists(debiandir):
        shutil.move(debiandir, os.path.join(srcdir, 'debian'))
}

###############################################################################
# do_debian_patch
###############################################################################

# Check Debian source format and then decide the action.
# The meanings of return values are the follows.
#   0: native package, there is no patch
#   1: 1.0 format or custom format, need to apply patches
#   3: 3.0 quilt format, need to apply patches by quilt
debian_check_source_format() {
	FORMAT_FILE=${S}/debian/source/format
	if [ ! -f ${FORMAT_FILE} ]; then
		bbnote "Debian source format is not defined, assume '1.0'"
		return 1
	fi
	FORMAT_VAL=$(cat ${FORMAT_FILE})
	bbnote "Debian source format is '${FORMAT_VAL}'"
	case "${FORMAT_VAL}" in
	"3.0 (native)")
		bbnote "nothing to do"
		return 0
		;;
	"3.0 (quilt)")
		return 3
		;;
	"3.0"*|"2.0"*)
		# FIXME: no information about how to handle
		bbfatal "unsupported source format"
		;;
	esac
	return 1
}

# Some 3.0 formatted source packages have no patch.
# Please set DEBIAN_QUILT_PATCHES = "" for such packages.
DEBIAN_QUILT_PATCHES ?= "${S}/debian/patches"

DEBIAN_QUILT_DIR ?= "${S}/.pc"
DEBIAN_QUILT_DIR_ESC ?= "${S}/.pc.debian"

# apply patches by quilt
debian_patch_quilt() {
	# confirm that other patches didn't applied
	if [ -d ${DEBIAN_QUILT_DIR} -o -d ${DEBIAN_QUILT_DIR_ESC} ]; then
		bbfatal "unknown quilt patches already applied"
	fi

	# some source packages don't have patch
	if [ -z "${DEBIAN_QUILT_PATCHES}" ]; then
		if [ -d ${DEBIAN_UNPACK_DIR}/debian/patches ]; then
			bbfatal "DEBIAN_QUILT_PATCHES is null, but ${DEBIAN_UNPACK_DIR}/debian/patches exists"
		fi
		bbnote "no debian patch exists in the source tree, nothing to do"
		return
	fi

	if [ ! -d ${DEBIAN_QUILT_PATCHES} ]; then
		bbfatal "${DEBIAN_QUILT_PATCHES} not found"
	elif [ ! -f ${DEBIAN_QUILT_PATCHES}/series ]; then
		bbfatal "${DEBIAN_QUILT_PATCHES}/series not found"
	# sometimes series is empty, it's too scary
	elif [ -z "$(sed '/^#/d' ${DEBIAN_QUILT_PATCHES}/series)" ]; then
		FOUND_PATCHES="$(debian_find_patches)"
		if [ -z "${FOUND_PATCHES}" ]; then
			bbnote "series is empty, nothing to do"
			return
		else
			bbfatal "series is empty, but some patches found"
		fi
	fi

	# apply patches
	QUILT_PATCHES=${DEBIAN_QUILT_PATCHES} quilt --quiltrc /dev/null push -a

	# avoid conflict with "do_patch"
	if [ -d ${DEBIAN_QUILT_DIR} ]; then
		mv ${DEBIAN_QUILT_DIR} ${DEBIAN_QUILT_DIR_ESC}
	fi
}

DEBIAN_DPATCH_PATCHES ?= "${S}/debian/patches"
# apply patches by dpatch
debian_patch_dpatch() {
	# Replace hardcode path in patch files
	find ${DEBIAN_DPATCH_PATCHES} -name "*.dpatch" -type f -exec sed -i \
	    -e "s@^#! /bin/sh /usr/share/dpatch/dpatch-run@#! /usr/bin/env dpatch-run@g" {} \;

	export PATH="${STAGING_DATADIR_NATIVE}/dpatch:$PATH"
	dpatch apply-all
}

DEBIAN_FIND_PATCHES_DIR ?= "${S}/debian"

debian_find_patches() {
	find ${DEBIAN_FIND_PATCHES_DIR} \
		-name "*.patch" -o \
		-name "*.diff" \
		-type f
}

# used only when DEBIAN_PATCH_TYPE is "abnormal"
# this is very rare case; should not be used except
# the cases that all other types cannot be used
# this function must be overwritten by each recipe
debian_patch_abnormal() {
	bbfatal "debian_patch_abnormal not defined"
}

# decide an action to apply patches for the source package
# candidates: quilt, dpatch, nopatch, abnormal
DEBIAN_PATCH_TYPE ?= ""

addtask debian_patch after do_unpack before do_patch
do_debian_patch[dirs] = "${S}"
do_debian_patch[depends] += "${@base_conditional(\
    'PN', 'quilt-native', '', 'quilt-native:do_populate_sysroot', d)}"
do_debian_patch[depends] += "${@base_conditional(\
    'DEBIAN_PATCH_TYPE', 'dpatch', 'dpatch-native:do_populate_sysroot', '', d)}"
do_debian_patch() {
	if debian_check_source_format; then
		return 0
	else
		FORMAT=$?
	fi
	# apply patches according to the source format
	case ${FORMAT} in
	1)
		# DEBIAN_PATCH_TYPE must be set manually to decide
		# an action when Debian source format is not 3.0
		if [ -z "${DEBIAN_PATCH_TYPE}" ]; then
			bbfatal "DEBIAN_PATCH_TYPE not set"
		fi

		bbnote "DEBIAN_PATCH_TYPE: ${DEBIAN_PATCH_TYPE}"
		if [ "${DEBIAN_PATCH_TYPE}" = "quilt" ]; then
			debian_patch_quilt
		elif [ "${DEBIAN_PATCH_TYPE}" = "dpatch" ]; then
			debian_patch_dpatch
		elif [ "${DEBIAN_PATCH_TYPE}" = "nopatch" ]; then
			# No patch and no function to apply patches in
			# some source packages. In such cases, confirm
			# that really no patch-related file is included
			FOUND_PATCHES=$(debian_find_patches)
			if [ -n "${FOUND_PATCHES}" ]; then
				bberror "the following patches found:"
				for p in ${FOUND_PATCHES}; do
					bberror ${p}
				done
				bbfatal "please re-consider DEBIAN_PATCH_TYPE"
			fi
		elif [ "${DEBIAN_PATCH_TYPE}" = "abnormal" ]; then
			debian_patch_abnormal
		else
			bbfatal "invalid DEBIAN_PATCH_TYPE: ${DEBIAN_PATCH_TYPE}"
		fi
		;;
	3)
		debian_patch_quilt
		;;
	esac
}
EXPORT_FUNCTIONS do_debian_patch
