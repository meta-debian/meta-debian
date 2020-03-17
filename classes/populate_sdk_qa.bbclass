# Check if unmet dependencies occured when do_populate_sdk
def populate_sdk_check_unmet_deps(d):
    import re

    logfile = d.getVar("BB_LOGFILE", True)
    with open(logfile, 'r') as f:
        unmet_deps_pattern = "^E: Unmet dependencies.*"
        for line in f:
            if re.match(unmet_deps_pattern, line):
                bb.warn("Unmet dependencies. See detail in %s." % logfile)

do_populate_sdk_append() {
    populate_sdk_check_unmet_deps(d)
}

