logo.pdf: clean logo.tex
	xelatex logo.tex
	pdf2svg logo.pdf logo.svg

open: logo.pdf
	open logo.pdf

clean:
	rm -rf *.log *.aux *.svg *.pdf
