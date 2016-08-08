dist/build: $(wildcard src/minesweeper/*.java) $(wildcard src/minesweeper/*/*.java)
	[ -f $@ ] || echo 0 > $@
	[ -d .tmp ] || mkdir .tmp
	javac -d .tmp $^ && build=`awk '{ print $$1 + 1 }' $@` && echo $$build > $@

dist/Minesweeper.jar: src/Manifest dist/build
	jar cfm $@ $< res -C .tmp minesweeper

.PHONY: all
all: dist/Minesweeper.jar

.PHONY: clean
clean:
	rm -rf .tmp
