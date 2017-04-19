#
# Base recipe: meta/recipes-support/nss/nss_3.15.1.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "Mozilla's SSL and TLS implementation"
DESCRIPTION = "Network Security Services (NSS) is a set of libraries \
designed to support cross-platform development of \
security-enabled client and server applications. \
Applications built with NSS can support SSL v2 and v3, \
TLS, PKCS 5, PKCS 7, PKCS 11, PKCS 12, S/MIME, X.509 \
v3 certificates, and other security standards."
HOMEPAGE = "http://www.mozilla.org/projects/security/pki/nss/"

PR = "r1"
DEPENDS = "sqlite3 nspr zlib nss-native"
DEPENDS_class-native = "sqlite3-native nspr-native zlib-native"
RDEPENDS_${PN} = "perl"

inherit siteinfo debian-package
PV = "3.26"

LICENSE = "MPLv2 | (MPLv2 & GPLv2+) | (MPLv2 & LGPLv2.1+)"
LIC_FILES_CHKSUM = "file://nss/COPYING;md5=3b1e88e1b9c0b5a4b2881d46cce06a18 \
                    file://nss/lib/freebl/mpi/doc/LICENSE;md5=491f158d09d948466afce85d6f1fe18f \
                    file://nss/lib/freebl/mpi/doc/LICENSE-MPL;md5=5d425c8f3157dbf212db2ec53d9e5132"

SRC_URI += "\                                                                    
    file://nss-fix-support-cross-compiling.patch \                              
    file://nss-no-rpath-for-cross-compiling.patch \                             
    file://nss-fix-incorrect-shebang-of-perl.patch \                            
    file://nss-fix-nsinstall-build.patch \
"

SRC_URI_append_class-target = "\
    file://nss.pc.in \
    file://signlibs.sh \
"

TD = "${S}/tentative-dist"
TDS = "${S}/tentative-dist-staging"

TARGET_CC_ARCH += "${LDFLAGS}"

do_compile_prepend_class-native() {
	export NSPR_INCLUDE_DIR=${STAGING_INCDIR_NATIVE}
	export NSPR_LIB_DIR=${STAGING_LIBDIR_NATIVE}
}

do_compile_prepend_class-nativesdk() {
	export LDFLAGS=""
}

do_compile() {
	export CROSS_COMPILE=1
	export NATIVE_CC="gcc"
	export BUILD_OPT=1

	export FREEBL_NO_DEPEND=1
	export FREEBL_LOWHASH=1

	export LIBDIR=${libdir}
	export MOZILLA_CLIENT=1
	export NS_USE_GCC=1
	export NSS_USE_SYSTEM_SQLITE=1
	export NSS_ENABLE_ECC=1

	export OS_RELEASE=3.4
	export OS_TARGET=Linux
	export OS_ARCH=Linux

	if [ "${TARGET_ARCH}" = "powerpc" ]; then
		OS_TEST=ppc
	elif [ "${TARGET_ARCH}" = "powerpc64" ]; then
		OS_TEST=ppc64
	elif [ "${TARGET_ARCH}" = "mips" -o "${TARGET_ARCH}" = "mipsel" -o "${TARGET_ARCH}" = "mips64" -o "${TARGET_ARCH}" = "mips64el" ]; then
		OS_TEST=mips
	else
		OS_TEST="${TARGET_ARCH}"
	fi

	if [ "${SITEINFO_BITS}" = "64" ]; then
		export USE_64=1
	fi

	make -C ./nss CCC="${CXX}" \
		OS_TEST=${OS_TEST}
}

do_install() {
	export CROSS_COMPILE=1
	export NATIVE_CC="gcc"
	export BUILD_OPT=1

	export FREEBL_NO_DEPEND=1

	export LIBDIR=${libdir}
	export MOZILLA_CLIENT=1
	export NS_USE_GCC=1
	export NSS_USE_SYSTEM_SQLITE=1
	export NSS_ENABLE_ECC=1

	export OS_RELEASE=3.4
	export OS_TARGET=Linux
	export OS_ARCH=Linux

	if [ "${TARGET_ARCH}" = "powerpc" ]; then
		OS_TEST=ppc
	elif [ "${TARGET_ARCH}" = "powerpc64" ]; then
		OS_TEST=ppc64
	elif [ "${TARGET_ARCH}" = "mips" -o "${TARGET_ARCH}" = "mipsel" -o "${TARGET_ARCH}" = "mips64" -o "${TARGET_ARCH}" = "mips64el" ]; then
		OS_TEST=mips
	else
		OS_TEST="${TARGET_ARCH}"
	fi
	if [ "${SITEINFO_BITS}" = "64" ]; then
		export USE_64=1
	fi

	make -C ./nss \
		CCC="${CXX}" \
		OS_TEST=${OS_TEST} \
		SOURCE_LIB_DIR="${TD}/${libdir}" \
		SOURCE_BIN_DIR="${TD}/${bindir}" \
		install

	install -d ${D}/${libdir}/
	for file in ${S}/dist/*.OBJ/lib/*.so; do
		echo "Installing `basename $file`..."
		cp $file  ${D}/${libdir}/
	done
	for shared_lib in ${TD}/${libdir}/*.so.*; do
		if [ -f $shared_lib ]; then
			cp $shared_lib ${D}/${libdir}
			ln -sf $(basename $shared_lib) ${D}/${libdir}/$(basename $shared_lib .1oe)
		fi
	done
	for shared_lib in ${TD}/${libdir}/*.so; do
		if [ -f $shared_lib -a ! -e ${D}/${libdir}/$shared_lib ]; then
			cp $shared_lib ${D}/${libdir}
		fi
	done

	install -d ${D}/${includedir}/nss3
	install -m 644 -t ${D}/${includedir}/nss3 dist/public/nss/*

	install -d ${D}/${bindir}
	for binary in ${TD}/${bindir}/*; do
		install -m 755 -t ${D}/${bindir} $binary
	done

	# Create soft links for libnss3-1d packages
	ln -sf libnss3.so ${D}${libdir}/libnss3.so.1d
	ln -sf libnssutil3.so ${D}${libdir}/libnssutil3.so.1d
	ln -sf libsmime3.so ${D}${libdir}/libsmime3.so.1d
	ln -sf libssl3.so ${D}${libdir}/libssl3.so.1d

	# Remove redundant binaries compare to list of file in Debian
	# packages
	rm ${D}/${bindir}/atob ${D}/${bindir}/baddbdir ${D}/${bindir}/bltest \
		${D}/${bindir}/btoa ${D}/${bindir}/certcgi \
		${D}/${bindir}/crmftest ${D}/${bindir}/dertimetest ${D}/${bindir}/smime \
		${D}/${bindir}/digest ${D}/${bindir}/encodeinttest ${D}/${bindir}/fipstest \
		${D}/${bindir}/listsuites ${D}/${bindir}/makepqg ${D}/${bindir}/mangle \
		${D}/${bindir}/multinit ${D}/${bindir}/nonspr10 ${D}/${bindir}/ocspresp \
		${D}/${bindir}/oidcalc ${D}/${bindir}/pk11gcmtest ${D}/${bindir}/pk11mode \
		${D}/${bindir}/pkix-errcodes ${D}/${bindir}/remtest ${D}/${bindir}/sdrtest \
		${D}/${bindir}/secmodtest

	mv ${D}${bindir}/addbuiltin ${D}${bindir}/nss-addbuiltin
	mv ${D}${bindir}/dbtest ${D}${bindir}/nss-db-test
	mv ${D}${bindir}/pp ${D}${bindir}/nss-pp
}

do_install_append_class-target() {
	# Create empty .chk files for the NSS libraries at build time. They could
	# be regenerated at target's boot time.
	for file in libsoftokn3.chk libfreebl3.chk libnssdbm3.chk; do
		touch ${D}/${libdir}/$file
		chmod 755 ${D}/${libdir}/$file
	done
	install -D -m 755 ${WORKDIR}/signlibs.sh ${D}/${bindir}/signlibs.sh

	install -d ${D}${libdir}/pkgconfig/
	sed 's/%NSS_VERSION%/${PV}/' ${WORKDIR}/nss.pc.in | sed 's/%NSPR_VERSION%/4.9.2/' > ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OEPREFIX:${prefix}:g ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OEEXECPREFIX:${exec_prefix}:g ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OELIBDIR:${libdir}:g ${D}${libdir}/pkgconfig/nss.pc
	sed -i s:OEINCDIR:${includedir}/nss3:g ${D}${libdir}/pkgconfig/nss.pc

	# Create a blank certificate
	mkdir -p ${D}/etc/pki/nssdb/
	touch ./empty_password
	certutil -N -d ${D}/etc/pki/nssdb/ -f ./empty_password
	chmod 644 ${D}/etc/pki/nssdb/*.db
	rm ./empty_password
}

pkg_postinst_${PN} () {
    if [ -n "$D" ]; then
        for I in $D/${libdir}/lib*.chk; do
            DN=`dirname $I`
            BN=`basename $I .chk`
            FN=$DN/$BN.so
            shlibsign -i $FN
            if [ $? -ne 0 ]; then
               exit 1
            fi
        done
        exit 0
    fi
    signlibs.sh
}

# Add more packages according to list of packages in Debian
PACKAGES = "lib${PN} lib${PN}-dbg lib${PN}-dev lib${PN}-tools lib${PN}-1d"

# Set correct files to new package
FILES_lib${PN}-1d = " \
	${libdir}/lib*.so.1d \
	"

FILES_lib${PN}-tools = " \
	${bindir} \
	"

FILES_lib${PN} = "\
	${sysconfdir} \
	${libdir}/lib*.chk \
	${libdir}/lib*.so \
	"

FILES_lib${PN}-dev = "\
	${libdir}/nss \
	${libdir}/pkgconfig/* \
	${includedir}/* \
	"

FILES_lib${PN}-dbg = "\
	${bindir}/.debug/* \
	${libdir}/.debug/* \
	/usr/src/debug \
	"

PKG_lib${PN} = "lib${PN}3"
PKG_lib${PN}-dbg = "lib${PN}3-dbg"
PKG_lib${PN}-dev = "lib${PN}3-dev"
PKG_lib${PN}-tools = "lib${PN}3-tools"
PKG_lib${PN}-1d = "lib${PN}3-1d"

BBCLASSEXTEND = "native nativesdk"
