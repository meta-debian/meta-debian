SUMMARY = "MySQL database"
DESCRIPTION = "\
MySQL is a fast, stable and true multi-user, multi-threaded SQL database \
server. SQL (Structured Query Language) is the most popular database query \
language in the world. The main goals of MySQL are speed, robustness and \
ease of use \
"
HOMEPAGE = "http://dev.mysql.com/"

inherit debian-package
PV = "5.5.57"
VER = "5.5"
DPN = "mysql-${VER}"

LICENSE = "GPLv2+ & BSD-4-Clause & BSD & Zlib & PD & ISC"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://cmd-line-utils/libedit/eln.c;beginline=3;endline=34;md5=4bb47d8f3baf56e0e5895920250769d5 \
	file://cmd-line-utils/libedit/chared.c;beginline=4;endline=33;md5=c9d1d3d967ea0ac8eb7ebf0078c97f77 \
	file://storage/archive/azlib.h;beginline=6;endline=34;md5=7c27ae0384929249664da410d539a1dc \
	file://scripts/mysqld_safe.sh;beginline=2;endline=12;md5=03abbf638cce91047edbd7e3f62b8382 \
	file://cmd-line-utils/libedit/np/strlcat.c;beginline=4;endline=18;md5=0842004924db787e8550a43e205891a7 \
	"

inherit cmake
