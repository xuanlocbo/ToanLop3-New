"""Bounded, bidirectional viewport search, independent of Android for unit tests."""
def sweep_viewports(read,match,advance,signature,max_views=32):
    """Inspect every viewport while descending, then ascend only at a confirmed end.

    Repeated identical hierarchies detect an end or stalled scroll. Two unchanged
    transitions tolerate a single dropped gesture. A hard cap keeps failures bounded.
    Never substitute an answer/fixture or return a nonmatching node.
    """
    if max_views<3:
        raise ValueError("max_views must be at least 3")
    tree=None
    for direction in (1,-1):
        previous=None
        stalled=0
        for attempt in range(max_views):
            tree=read()
            node=match(tree)
            if node is not None:
                return node,tree
            current=signature(tree)
            stalled=stalled+1 if current==previous else 0
            previous=current
            if stalled>=2 or attempt==max_views-1:
                break
            if not advance(tree,direction):
                break
    return None,tree
