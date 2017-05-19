#
# base recipe: meta/recipes-support/boost/boost_1.55.0.bb
# base branch: daisy
#

include boost.inc

PR = "r1"

SUMMARY = "Free peer-reviewed portable C++ source libraries"
DEPENDS = "bjam-native zlib bzip2"

ARM_INSTRUCTION_SET = "arm"

BOOST_LIBS = "\
	atomic \
	chrono \
	date_time \
	exception \
	filesystem \
	graph \
	iostreams \
	locale \
	log \
	math \
	mpi \
	program_options \
	random \
	regex \
	serialization \
	signals \
	system \
	test \
	thread \
	timer \
	wave \
	"

# optional boost-python library
PACKAGECONFIG ??= "python"
PACKAGECONFIG[python] = ",,python"
BOOST_LIBS += "${@bb.utils.contains('PACKAGECONFIG', 'python', 'python', '', d)}"
inherit python-dir
PYTHON_ROOT = "${STAGING_DIR_HOST}/${prefix}"

# Make a package for each library, plus -dev
PACKAGES = "${PN}-dbg ${BOOST_PACKAGES}"
python __anonymous () {
    packages = []
    extras = []
    for lib in d.getVar('BOOST_LIBS', True).split( ):
            pkg = "boost-%s" % lib.replace("_", "-")
            extras.append("--with-%s" % lib)
            packages.append(pkg)
            if not d.getVar("FILES_%s" % pkg, True):
                    d.setVar("FILES_%s" % pkg, "${libdir}/libboost_%s*.so.*" % lib)
    d.setVar("BOOST_PACKAGES", " ".join(packages))
    d.setVar("BJAM_EXTRA", " ".join(extras))
}

# Override the contents of specific packages
FILES_boost-serialization = "${libdir}/libboost_serialization*.so.* \
	${libdir}/libboost_wserialization*.so.*"
FILES_boost-test = "${libdir}/libboost_prg_exec_monitor*.so.* \
	${libdir}/libboost_unit_test_framework*.so.*"

# -dev last to pick up the remaining stuff
PACKAGES += "${PN}-dev ${PN}-staticdev"
FILES_${PN}-dev = "${includedir} ${libdir}/libboost_*.so"
FILES_${PN}-staticdev = "${libdir}/libboost_*.a"

# "boost" is a metapackage which pulls in all boost librabries
PACKAGES += "${PN}"
RRECOMMENDS_${PN} += "${BOOST_PACKAGES}"
RRECOMMENDS_${PN}_class-native = ""
ALLOW_EMPTY_${PN} = "1"

# to avoid GNU_HASH QA errors added LDFLAGS to ARCH; a little bit dirty but at least it works
TARGET_CC_ARCH += " ${LDFLAGS}"

# Oh yippee, a new build system, it's sooo cooool I could eat my own
# foot.  inlining=on lets the compiler choose, I think.  At least this
# stuff is documented...
# NOTE: if you leave <debug-symbols>on then in a debug build the build sys
# objcopy will be invoked, and that won't work.  Building debug apparently
# requires hacking gcc-tools.jam
#
# Sometimes I wake up screaming.  Famous figures are gathered in the nightmare,
# Steve Bourne, Larry Wall, the whole of the ANSI C committee.  They're just
# standing there, waiting, but the truely terrifying thing is what they carry
# in their hands.  At first sight each seems to bear the same thing, but it is
# not so for the forms in their grasp are ever so slightly different one from
# the other.  Each is twisted in some grotesque way from the other to make each
# an unspeakable perversion impossible to perceive without the onset of madness.
# True insanity awaits anyone who perceives all of these horrors together.
#
# Quotation marks, there might be an easier way to do this, but I can't find
# it.  The problem is that the user.hpp configuration file must receive a
# pre-processor macro defined as the appropriate string - complete with "'s
# around it.  (<> is a possibility here but the danger to that is that the
# failure case interprets the < and > as shell redirections, creating
# random files in the source tree.)
#
#bjam: '-DBOOST_PLATFORM_CONFIG=\"config\"'
#do_compile: '-sGCC=... '"'-DBOOST_PLATFORM_CONFIG=\"config\"'"
SQD = '"'
EQD = '\"'
#boost.bb:   "...  '-sGCC=... '${SQD}'-DBOOST_PLATFORM_CONFIG=${EQD}config${EQD}'${SQD} ..."
BJAM_CONF = "${SQD}'-DBOOST_PLATFORM_CONFIG=${EQD}boost/config/platform/${TARGET_OS}.hpp${EQD}'${SQD}"

BJAM_TOOLS   = "-sTOOLS=gcc \
		'-sGCC=${CC} '${BJAM_CONF} \
		'-sGXX=${CXX} '${BJAM_CONF} \
		'-sGCC_INCLUDE_DIRECTORY=${STAGING_INCDIR}' \
		'-sGCC_STDLIB_DIRECTORY=${STAGING_LIBDIR}' \
		'-sBUILD=release <optimization>space <threading>multi <inlining>on <debug-symbols>off' \
		'-sPYTHON_VERSION=${PYTHON_BASEVERSION}' \
		'-sPYTHON_ROOT=${PYTHON_ROOT}' \
		'--layout=system' \
		"

# use PARALLEL_MAKE to speed up the build, but limit it by -j 64, greater paralelism causes bjam to segfault or to ignore -j
# https://svn.boost.org/trac/boost/ticket/7634
def get_boost_parallel_make(bb, d):
    pm = d.getVar('PARALLEL_MAKE', True)
    if pm:
        # people are usually using "-jN" or "-j N", but let it work with something else appended to it
        import re
        pm_prefix = re.search("\D+", pm)
        pm_val = re.search("\d+", pm)
        if pm_prefix is None or pm_val is None:
            bb.error("Unable to analyse format of PARALLEL_MAKE variable: %s" % pm)
        pm_nval = min(64, int(pm_val.group(0)))
        return pm_prefix.group(0) + str(pm_nval) + pm[pm_val.end():]
    else:
        return ""

BOOST_PARALLEL_MAKE = "${@get_boost_parallel_make(bb, d)}"
BJAM_OPTS    = '${BOOST_PARALLEL_MAKE} \
		${BJAM_TOOLS} \
		-sBOOST_BUILD_USER_CONFIG=${S}/tools/build/v2/user-config.jam \
		--builddir=${S}/${TARGET_SYS} \
		--disable-icu \
		${BJAM_EXTRA}'

# Native compilation of bzip2 isn't working
BJAM_OPTS_append_class-native = ' -sNO_BZIP2=1'

do_boostconfig() {
	cp -f boost/config/platform/linux.hpp boost/config/platform/linux-gnueabi.hpp

	# D2194:Fixing the failure of "error: duplicate initialization of gcc with the following parameters" during compilation.
	if ! grep -qe "^using gcc : 4.3.1" ${S}/tools/build/v2/user-config.jam
	then
		echo 'using gcc : 4.3.1 : ${CXX} : <cflags>"${CFLAGS}" <cxxflags>"${CXXFLAGS}" <linkflags>"${LDFLAGS}" ;' \
		     >> ${S}/tools/build/v2/user-config.jam
	fi

	echo "using python : ${PYTHON_BASEVERSION} : : ${STAGING_INCDIR}/python${PYTHON_BASEVERSION} ;" \
	     >> ${S}/tools/build/v2/user-config.jam

	CC="${BUILD_CC}" CFLAGS="${BUILD_CFLAGS}" ./bootstrap.sh --with-bjam=bjam --with-toolset=gcc --with-python-root=${PYTHON_ROOT}
	sed -i '/^using python/d' project-config.jam
}

do_boostconfig[dirs] = "${S}"
addtask do_boostconfig after do_patch before do_configure

do_compile() {
	set -ex
	bjam ${BJAM_OPTS} --prefix=${prefix} \
		--exec-prefix=${exec_prefix} \
		--libdir=${libdir} \
		--includedir=${includedir}
}

do_install() {
	set -ex
	bjam ${BJAM_OPTS} \
		--libdir=${D}${libdir} \
		--includedir=${D}${includedir} \
		install
	for lib in ${BOOST_LIBS}; do
		if [ -e ${D}${libdir}/libboost_${lib}.a ]; then
			ln -s libboost_${lib}.a ${D}${libdir}/libboost_${lib}-mt.a
		fi
		if [ -e ${D}${libdir}/libboost_${lib}.so ]; then
			ln -s libboost_${lib}.so ${D}${libdir}/libboost_${lib}-mt.so
		fi
	done

}

BBCLASSEXTEND = "native nativesdk"
