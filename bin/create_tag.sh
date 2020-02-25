#!/usr/bin/env bash


usage() { echo "Usage: $0 [-n Skip fetching tags]" 1>&2; exit 1; }

while getopts "n:" o; do
    case "${o}" in
        n)
            no_fetch=1
            ;;
        *)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

echo "Creating new tag"
echo ""

if [[ -z "${no_fetch}" ]]; then
    echo "- Fetching tags..."
    git fetch --tags
    echo
else
    echo "- Fetching tags skipped"
    echo
fi


current_branch=$(git rev-parse --abbrev-ref HEAD)

echo "- Creating git tag on branch '${current_branch}'..."

default_major_version=0
default_minor_version=0
default_patch_version=1

last_tag=$(git tag -l --sort=v:refname | tail -n 1)

echo "  Last tag is '$last_tag'"

last_tag_info=(${last_tag//./ })
last_major_version=${last_tag_info[0]:-${default_major_version}}
last_minor_version=${last_tag_info[1]:-${default_minor_version}}
last_patch_version=${last_tag_info[2]:-${default_patch_version}}

patch_version=$((10#$last_patch_version + 1))

new_tag="$last_major_version.$last_minor_version.$patch_version"

echo ""
echo "- Trying to create tag '$new_tag'"

if ! (git tag "$new_tag"); then
    echo "Failed to create tag '${new_tag}'" 1>&2; exit 1;
    exit
fi

git push --progress origin ${new_tag}
if [[ $? -eq 0 ]]; then
    echo ""
    echo "Tag '${new_tag}' successfully created and pushed"
else
    echo "Failed to push tag '${new_tag}'" 1>&2; exit 1;
fi
