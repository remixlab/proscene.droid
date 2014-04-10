ProScene.droid
========

# Description

**ProScene.droid** is a Android [ProScene](http://forum.processing.org/search/proscene). port. 

# Hacking

## Initial setup (you don't need this!)

First (and only) time setup. This is just for documentation purposes. Please visit the next sections.

```sh
git clone https://github.com/remixlab/proscene.droid.git
cd proscene.droid
git remote add -f bias https://github.com/remixlab/bias_tree.git
git subtree add --prefix src/remixlab/bias bias master --squash
git remote add -f fpstiming https://github.com/remixlab/fpstiming_tree.git
git subtree add --prefix src/remixlab/fpstiming fpstiming master --squash
git remote add -f dandelion https://github.com/remixlab/dandelion_tree.git
git subtree add --prefix src/remixlab/dandelion dandelion master --squash
git remote add -f util https://github.com/remixlab/util_tree.git
git subtree add --prefix src/remixlab/util util master --squash
```

## Read-only access setup

Use it as any other basic github repo, i.e.,:

```sh
# clone it:
git clone https://github.com/remixlab/proscene.droid.git
cd proscene
# pull changes in:
# for pull requests simply refer to: https://help.github.com/articles/using-pull-requests
```

## Read-write access setup

Clone the repo and add the remotes (here we refer to them as ["subtrees"](http://blogs.atlassian.com/2013/05/alternatives-to-git-submodule-git-subtree/)):

```sh
git clone https://github.com/remixlab/proscene.droid.git
cd proscene.droid
git remote add -f bias https://github.com/remixlab/bias_tree.git
git remote add -f fpstiming https://github.com/remixlab/fpstiming_tree.git
git remote add -f dandelion https://github.com/remixlab/dandelion_tree.git
git remote add -f util https://github.com/remixlab/util_tree.git
```

Update from time to time:

```sh
#fetching command:
git fetch <remote> master
git subtree pull --prefix src/remixlab/<remote> <remote> master --squash
```

To contribute back to upstream:

```sh
git push
```

To contribute to a particular subtree (i.e., bias, fpstiming, dandelion, or util)

```sh
git subtree push --prefix=src/remixlab/<remote> <remote> master
```

# Acknowledgements

To come...

