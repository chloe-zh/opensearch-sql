#!/bin/bash

# SPDX-License-Identifier: Apache-2.0
#
# The OpenSearch Contributors require contributions made to
# this file be licensed under the Apache-2.0 license or a
# compatible open source license.
#
# Modifications Copyright OpenSearch Contributors. See
# GitHub history for details.

RESULTS_FILE=cppcheck-results.log

# --force: force checks all define combinations (default max is 12)
# -iaws-sdk-cpp: avoid checking AWS C++ SDK source files in our repo
# -UWIN32: do not check WIN32-defined codepaths; this would throw errors on Mac
cppcheck --force -iaws-sdk-cpp -UWIN32 ./src 2> ${RESULTS_FILE}

if [ -s ${RESULTS_FILE} ]; then
    echo "!! Cppcheck errors found! Check ${RESULTS_FILE} for details."
    exit 1
else
    echo "No Cppcheck errors found."
fi