SUMMARY = "Emulate IO::File interface for in-core strings"
DESCRIPTION = "The IO::String module provide the IO::File interface for in-core\n\
strings.  An IO::String object can be attached to a string, and\n\
will make it possible to use the normal file operations for reading or\n\
writing data, as well as seeking to various locations of the string.\n\
The main reason you might want to do this, is if you have some other\n\
library module that only provide an interface to file handles, and you\n\
want to keep all the stuff in memory.\n\
.\n\
The IO::String module provide an interface compatible with\n\
IO::File as distributed with IO-1.20, but the following methods\n\
are not available; new_from_fd, fdopen, format_write,\n\
format_page_number, format_lines_per_page, format_lines_left,\n\
format_name, format_top_name."
HOMEPAGE = "https://metacpan.org/release/IO-String/"

inherit debian-package
PV = "1.08"

LICENSE = "Artistic-1.0 | GPL-1.0"
LIC_FILES_CHKSUM = "file://README;beginline=16;endline=19;md5=9ec31d28c1f3dbb643ce3ca6f890a0b0"

# There are no debian/patches but debian/source/format is "3.0 (quilt)"
DEBIAN_QUILT_PATCHES = ""

inherit allarch cpan

BBCLASSEXTEND = "native"
