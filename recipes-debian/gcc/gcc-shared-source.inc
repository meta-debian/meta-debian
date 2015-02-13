DEPENDS += " gcc-source"

# Because of sharing source code, these task have been done with 
# gcc-source_debian.bb recipe
deltask do_fetch
deltask do_unpack
deltask do_patch

do_configure[depends] += "gcc-source:do_patch"
do_populate_lic[depends] += "gcc-source:do_unpack"
