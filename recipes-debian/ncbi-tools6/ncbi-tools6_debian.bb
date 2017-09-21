SUMMARY = "NCBI libraries for graphic biology applications (debugging symbols)"
DESCRIPTION = "The NCBI Software Development Toolkit was developed for the production and \
 distribution of GenBank, Entrez, BLAST, and related services by NCBI.  It \
 allows you to read and write NCBI ASN.1 files, builds Blast or Entrez, etc."
HOMEPAGE = "http://www.ncbi.nlm.nih.gov/IEB/ToolBox/"

inherit debian-package
PV = "6.1.20120620"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://tools/actutils.c;beginline=3;endline=25;md5=f6c3e650cfb094790261046ae1f1eaa0"

# Add links the library files for compile
SRC_URI += "file://Fix-missing-library-to-compile_debian.patch"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools update-alternatives
B = "${S}/build"

DEPENDS += "libxmu motif libglu libpcre libpng csh-native"
export NCBI_VERSION_MAJOR = "6"
export NCBI_VERSION_MINOR= "1.20120620"
do_compile() {
	# Follow debian/rules
	export LD_LIBRARY_PATH=${S}/shlib:$LD_LIBRARY_PATH
	export NCBI_LBSM_SRC=ncbi_lbsmd_stub.c
	export NCBI_LBSM_OBJ=ncbi_lbsmd_stub.o

	LDFLAGS1="${CFLAGS} ${LDFLAGS}"
	VIBLIBS="-lXm -lXmu -lXt -lX11"
	OTHERLIBS="-lm"
	VIBFLAG="-DWIN_MOTIF"
	USESHLIB="NCBI_LINKINGLIBDIR="../shlib""
	MAKESHLIB="${USESHLIB} NCBI_SHLIBS=shlib"
	CFLAGS_PIC="${CFLAGS} -fPIC"
	PNG_INCLUDE="-D_PNG"
	OGL_LIBVARS="LIB400=libvibrantOGL.a LIB3000=libncbicn3dOGL.a"
	NETENTREZVERSION="2.02c2ASN1SPEC6"
	OGLLIBS="-lGLU -lGL"
	PNG_LIBS="-lpng"
	OTHERS="libncbimla.a libnetblast.a libncbitxc2.a libncbiid1.a shlib"
	THREAD_OBJ="ncbithr.o"
	MT_OTHERLIBS="-lpthread"
	OGL_TARGETS="Cn3D"
	MTAPPS="blast blastall blastall_old blastpgp seedtop megablast \
	        rpsblast blastclust"
	VIB="Psequin sbtedit udv ddv blastcl3 taxblast idfetch bl2seq asn2gb tbl2asn \
	gene2xml entrez2 gbseqget asn2all asn2asn asn2fsa asn2xml asndisc \
	asnmacro asnval cleanasn insdseqget nps2gps spidey trna2sap trna2tbl \
	${OGL_TARGETS}"

	ln -sf ../make/*.unx .
	sed -i -e "s|!/bin/csh|!${STAGING_DIR_NATIVE}${base_bindir_native}/csh|" ${S}/make/ln-if-absent
	local shebang_length=$(head -n 1 ${S}/make/ln-if-absent | wc -m)
	if [ ${shebang_length} -gt 127 ]; then
		sed -i -e "s|!${STAGING_DIR_NATIVE}${base_bindir_native}/csh -f|!/usr/bin/env csh|" ${S}/make/ln-if-absent
	fi
	ln -sf ../make/ln-if-absent build
	mv makeall.unx makefile
	chmod +x ${S}/debian/makemenu

	oe_runmake -C ${S}/build all \
		CC="${CC}" LDFLAGS1="${LDFLAGS1}" LCL=lnx \
		AR="${AR}" RAN="${RANLIB}" OTHERLIBS="${OTHERLIBS}" \
		VIBLIBS="${VIBLIBS}" VIBFLAG="${VIBFLAG}" \
		${USESHLIB} \
		CFLAGS1="-c ${CFLAGS_PIC} ${PNG_INCLUDE}" \
		LIB4=libvibrant.a LIB20=libncbidesk.a LIB28=libvibgif.a \
		LIB30=libncbicn3d.a LIB45=libddvlib.a ${OGL_LIBVARS}

	oe_runmake -C ${S}/build -f makenet.unx \
		CC="${CC}" LDFLAGS1="${LDFLAGS1}" LCL=lnx \
		AR="${AR}" RAN="${RANLIB}" OTHERLIBS="${OTHERLIBS}" \
		VIBLIBS="${VIBLIBS}" VIBFLAG="${VIBFLAG}" \
		${USESHLIB} \
		CFLAGS1="-c ${CFLAGS_PIC}" \
		LDFLAGS="${LDFLAGS}" \
		NETENTREZVERSION="${NETENTREZVERSION}" \
		BLIB31=libvibnet.a OGLLIBS="${OGLLIBS} ${PNG_LIBS}" \
		all ${OTHERS}

	# Clear out the PIC objects
	oe_runmake -C ${S}/build clean
	
	oe_runmake -C ${S}/build all \
		CC="${CC}" LDFLAGS1="${LDFLAGS1}" LCL=lnx \
		AR="${AR}" RAN="${RANLIB}" OTHERLIBS="${OTHERLIBS}" \
		VIBLIBS="${VIBLIBS}" VIBFLAG="${VIBFLAG}" \
		${USESHLIB} \
		CFLAGS1="-c ${CFLAGS} ${PNG_INCLUDE}" \
		LIB4=libvibrant.a LIB20=libncbidesk.a LIB28=libvibgif.a \
		LIB30=libncbicn3d.a LIB45=libddvlib.a ${OGL_LIBVARS}

	oe_runmake -C ${S}/build -f makedemo.unx \
		CC="${CC}" LDFLAGS1="${LDFLAGS1}" LCL=lnx \
		AR="${AR}" RAN="${RANLIB}" OTHERLIBS="${OTHERLIBS} -lnetcli" \
		VIBLIBS="${VIBLIBS}" VIBFLAG="${VIBFLAG}" \
		${USESHLIB} \
		CFLAGS1="-c ${CFLAGS}" \
		VIBLIBS= VIBFLAG= LIB50=-lpcre
		cd ${S}/build & rm -f ${MTAPPS}
		
	oe_runmake -C ${S}/build -f makedemo.unx \
		CC="${CC}" LDFLAGS1="${LDFLAGS1}" LCL=lnx \
		AR="${AR}" RAN="${RANLIB}" OTHERLIBS="${OTHERLIBS}" \
		VIBLIBS="${VIBLIBS}" VIBFLAG="${VIBFLAG}" \
		${USESHLIB} \
		CFLAGS1="-c ${CFLAGS}" \
		VIBLIBS= VIBFLAG= THREAD_OBJ="${THREAD_OBJ}" \
		THREAD_OTHERLIBS="${MT_OTHERLIBS}" ${MTAPPS}

	oe_runmake -C ${S}/build -f makenet.unx \
		CC="${CC}" LDFLAGS1="${LDFLAGS1}" LCL=lnx \
		AR="${AR}" RAN="${RANLIB}" OTHERLIBS="${OTHERLIBS}" \
		VIBLIBS="${VIBLIBS}" VIBFLAG="${VIBFLAG}" \
		${USESHLIB} \
		CFLAGS1="-c ${CFLAGS}" \
		THREAD_OBJ="${THREAD_OBJ}" \
		THREAD_OTHERLIBS="${MT_OTHERLIBS}" \
		NETENTREZVERSION="${NETENTREZVERSION}" BLIB31=libvibnet.a \
		OGLLIBS= VIBLIBS= VIB="${VIB}"
}

# Base on debian/rules
do_install() {
	install -d ${D}${libdir}
	install -m 0644 ${S}/lib/* \
		${S}/shlib/*.so.${NCBI_VERSION_MAJOR}.${NCBI_VERSION_MINOR} \
		${D}${libdir}
	for x in ncbiacc ncbiCacc netentr; do
		rm -f ${D}${libdir}/lib${x}.so.${NCBI_VERSION_MAJOR}.${NCBI_VERSION_MINOR}
		ln -s libncbiNacc.so.${NCBI_VERSION_MAJOR} \
			${D}${libdir}/lib${x}.so.${NCBI_VERSION_MAJOR}
		ln -s libncbiNacc.so ${D}${libdir}/lib${x}.so
	done

	for x in ncbicn3d vibrant; do
		rm -f ${D}${libdir}/lib${x}.so.${NCBI_VERSION_MAJOR}.${NCBI_VERSION_MINOR}
		ln -s lib${x}OGL.so.${NCBI_VERSION_MAJOR} \
			${D}${libdir}/lib${x}.so.${NCBI_VERSION_MAJOR}
		ln -s lib${x}OGL.so ${D}${libdir}/lib${x}.so
	done
	
	rm -f ${D}${libdir}/libregexp.*

	cd ${D}${libdir}
	for f in *.so.${NCBI_VERSION_MAJOR}.${NCBI_VERSION_MINOR}; do
		base=`basename $f .so.${NCBI_VERSION_MAJOR}.${NCBI_VERSION_MINOR}`
		ln -s $f ${base}.so.${NCBI_VERSION_MAJOR}
		ln -s $f ${base}.so
	done

	install -d ${D}${includedir}/ncbi
	cp -LRp ${S}/include/* ${D}${includedir}/ncbi
	cd ${D}${includedir}/ncbi && \
		rm -f FSpCompat.h FullPath.h More*.h Optimization*.h pcre*.h
	find ${D}${includedir} -type f | xargs chmod 644

	install -d ${D}${bindir}
	install `find ${S}/build -type f -perm /111 -print` \
		${D}${bindir}
	rm -f ${D}${bindir}/*test* \
	      ${D}${bindir}/*demo* \
	      ${D}${bindir}/dosimple \
	      ${D}${bindir}/ncbisort \
	      ${D}${bindir}/cdscan \
	      ${D}${bindir}/entrcmd \
	      ${D}${bindir}/blast.REAL
	mv ${D}${bindir}/Cn3D ${D}${bindir}/Cn3D-3.0
	mv ${D}${bindir}/blast ${D}${bindir}/blast2

	install -d ${D}${sysconfdir}/ncbi
	install -m 0644 ${S}/debian/.*rc ${D}${sysconfdir}/ncbi
	install ${S}/debian/vibrate ${D}${bindir}
	
	install -d ${D}${datadir}/ncbi/data
	install -m 0644 ${S}/data/* ${D}${datadir}/ncbi/data

	# Follow debian/ncbi-tools-x11.links
	ln -s entrez2 ${D}${bindir}/entrez
	ln -s Psequin ${D}${bindir}/sequin

	install -d ${D}${libdir}/mime/packages
	install -m 0644 ${S}/debian/ncbi-tools-x11.mime \
		${D}${libdir}/mime/packages/ncbi-tools-x11
	
}

PACKAGES =+ "blast libvibrant6b libncbi ncbi-rrna-data ncbi-data ncbi-tools-x11"

FILES_blast = "${bindir}/*blast* \
               ${bindir}/bl2seq \
               ${bindir}/copymat \
               ${bindir}/fastacmd \
               ${bindir}/formatdb \
               ${bindir}/formatrpsdb \
               ${bindir}/impala \
               ${bindir}/makemat \
               ${bindir}/seedtop"

FILES_libncbi = "${libdir}/*${SOLIBS}"

FILES_libvibrant6b = "${libdir}/libddvlib${SOLIBS} \
                      ${libdir}/libncbicn3d${SOLIBS} \
                      ${libdir}/libncbicn3dOGL${SOLIBS} \
                      ${libdir}/libncbidesk${SOLIBS} \
                      ${libdir}/libvibnet${SOLIBS} \
                      ${libdir}/libvibrant${SOLIBS} \
                      ${libdir}/libvibrantOGL${SOLIBS}"
FILES_ncbi-data = "${sysconfdir} \
                   ${bindir}/vibrate \
                   ${datadir}/ncbi/* \
                   ${datadir}/pixmaps/*"

FILES_ncbi-rrna-data = "${datadir}/ncbi/data/Combined16SrRNA_* \
                        ${datadir}/ncbi/data/LSURef_* \
                        ${datadir}/ncbi/data/SSURef_* \
                        ${datadir}/ncbi/data/rRNA_blast.nal"
FILES_ncbi-tools-x11 = "${bindir}/Cn3D-3.0 \
                        ${bindir}/Psequin \
                        ${bindir}/ddv \
                        ${bindir}/entrez* \
                        ${bindir}/sbtedit \
                        ${bindir}/udv \
                        ${bindir}/sequin \
                        ${libdir}/mime \
                        ${datadir}/applications"

PKG_${PN} = "ncbi-tools-bin"
PKG_blast = "blast2"
PKG_libncbi = "libncbi6"
RPROVIDES_${PN} += "ncbi-tools-bin"
RPROVIDES_blast += "blast2"
RPROVIDES_libncbi += "libncbi6"
RPROVIDES_${PN}-dev += "libncbi6-dev libvibrant6-dev"
RDEPENDS_libncbi += "ncbi-data"
RDEPENDS_ncbi-rrna-data += "ncbi-data"
RRECOMMENDS_ncbi-rrna-data += "blast2"
RDEPENDS_${PN} += "libncbi6"
RSUGGESTS_${PN} += "blast2 libvibrant6b ncbi-tools-x11"
RDEPENDS_ncbi-tools-x11 += "libncbi6 libvibrant6b"
RSUGGESTS_ncbi-tools-x11 += "blast2 ncbi-tools-bin"
RDEPENDS_blast += "libncbi6"
RDEPENDS_libvibrant6b += "libncbi6"
RDEPENDS_${PN}-dev += "libglu-dev libmotif-dev libvibrant6b libxmu-dev"

# Follow debian/ncbi-tools-x11.postinst
ALTERNATIVE_ncbi-tools-x11 = "Cn3D"
ALTERNATIVE_PRIORITY = "30"
ALTERNATIVE_LINK_NAME[Cn3D] = "${bindir}/Cn3D"
ALTERNATIVE_TARGET[Cn3D] = "${bindir}/Cn3D-3.0"

# Avoid a parallel build problem
PARALLEL_MAKE = ""
