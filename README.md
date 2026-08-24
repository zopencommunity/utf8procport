# utf8proc for z/OS

[utf8proc](https://github.com/JuliaStrings/utf8proc) 2.11.3 — a small,
self-contained C library for UTF-8 and Unicode, ported to z/OS.

Normalisation (NFC/NFD/NFKC/NFKD), case folding, grapheme breaking and
character properties. It carries its own copy of the Unicode tables, so it has
no dependencies at all — which is why so many projects link or vendor it,
Arrow and Julia among them.

## Installing

```sh
zopen install utf8proc
```

## Using it from another port

```sh
export ZOPEN_STABLE_DEPS="... utf8proc"
```

contributes `-I<prefix>/include`, `-L<prefix>/lib` and `-lutf8proc`.

```c
#include <utf8proc.h>

/* compose "e" + combining acute into a single codepoint */
utf8proc_uint8_t *nfc = utf8proc_NFC((const utf8proc_uint8_t *)"e\xcc\x81");
/* nfc is now "\xc3\xa9" */
free(nfc);
```

## Notes on the port

**No patches.** utf8proc builds clean on z/OS. It is table-driven C with no
platform assumptions and no OS calls beyond the standard library, so it needs
neither patching nor zoslib.

Upstream's own test suite downloads Unicode data files to compare against,
which is not something to do inside a build, so it is not run. The port
supplies its own check instead, compiled against the **installed** library:

| check | what it would catch |
| --- | --- |
| version string is present | a library that linked but is not initialised |
| decodes a two-byte sequence | broken UTF-8 iteration |
| encodes it back to the same bytes | an encode/decode asymmetry |
| NFC composes e + combining acute | mis-built normalisation tables — the operation callers actually want |
| reports the category of a codepoint | a corrupt property table |
| uppercases a non-ASCII letter | broken case mapping |

The normalisation check is the load-bearing one: it is the first thing that
would break if the Unicode tables were built or byte-ordered wrongly, and it
would break silently.
