# MOD (taiga town): copies mod/town_map.txt into TaigaTownLevel.MAP
#
#   python mod/map_to_java.py
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVA = os.path.join(ROOT, 'core', 'src', 'main', 'java', 'com', 'shatteredpixel', 'shatteredpixeldungeon',
					'taiga', 'TaigaTownLevel.java')

BACKSLASH = chr(92)
QUOTE = chr(34)

rows = [l.rstrip('\n') for l in open(os.path.join(ROOT, 'mod', 'town_map.txt')) if l.strip()]
assert len(set(len(r) for r in rows)) == 1, 'all map rows must have the same length'


def java_string(r):
	return QUOTE + r.replace(BACKSLASH, BACKSLASH * 2).replace(QUOTE, BACKSLASH + QUOTE) + QUOTE


body = ''.join('\t\t\t' + java_string(r) + ',\n' for r in rows)
src = open(JAVA, encoding='utf-8').read()
pattern = re.compile(r'(//MAP-BEGIN[^\n]*\n\tpublic static final String\[\] MAP = \{\n).*?(\t\};\n\t//MAP-END)', re.S)
assert pattern.search(src), 'MAP markers not found'
src = pattern.sub(lambda m: m.group(1) + body + m.group(2), src)
open(JAVA, 'w', encoding='utf-8').write(src)
print('map: %dx%d' % (len(rows[0]), len(rows)))
