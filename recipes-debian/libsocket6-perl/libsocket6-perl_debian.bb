#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-cgl/tree/meta-cgl-common/recipes-perl/perl
# base commit: 51c601ee5a25ced6154b4b87f9c1b40838423e6f
#

SUMMARY = "Perl extensions for IPv6"
DESCRIPTION = "The Socket6 module supports getaddrinfo() and getnameinfo() to intend \
to enable protocol independent programming. If your environment \
supports IPv6, IPv6 related defines such as AF_INET6 are included."
HOMEPAGE = "https://metacpan.org/release/Socket6"

PR = "r0"

inherit debian-package
PV = "0.25"

LICENSE = "BSD-3-Clause & ISC"
LIC_FILES_CHKSUM = " \
    file://README;beginline=31;md5=4193e9db290f6adba830af0ffbae842a \
    file://inet_ntop.c;endline=16;md5=d202009b255fffe6953a0d61c2d6fab0 \
"

inherit cpan

CFLAGS += "-D_LARGEFILE_SOURCE -D_LARGEFILE64_SOURCE"
BUILD_CFLAGS += "-D_LARGEFILE_SOURCE -D_LARGEFILE64_SOURCE"

do_configure_prepend() {
	mkdir -p m4
	autoreconf -Wcross --verbose --install --force || oefatal "autoreconf execution failed."
	sed -i 's:\./configure\(.[^-]\):./configure --build=${BUILD_SYS} --host=${HOST_SYS} \\\\\
	        --target=${TARGET_SYS} --prefix=${prefix} --exec_prefix=${exec_prefix} \\\\\
	        --bindir=${bindir} --sbindir=${sbindir} --libexecdir=${libexecdir} \\\\\
	        --datadir=${datadir} --sysconfdir=${sysconfdir} \\\\\
	        --sharedstatedir=${sharedstatedir} --localstatedir=${localstatedir} \\\\\
	        --libdir=${libdir} --includedir=${includedir} --oldincludedir=${oldincludedir} \\\\\
	        --infodir=${infodir} --mandir=${mandir}\1:' ${S}/Makefile.PL
}

BBCLASSEXTEND = "native"
