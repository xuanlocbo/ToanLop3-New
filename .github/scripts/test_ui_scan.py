"""Regression for the p07 selector failure: never reverse at an arbitrary seven swipes."""
import unittest
from ui_scan import sweep_viewports

class SimulatedViewport:
    def __init__(self,pages,start=0,drop_once=False):
        self.pages=pages
        self.pos=start
        self.moves=[]
        self.drop_once=drop_once
    def read(self):
        return self.pos
    def move(self,view,direction):
        assert view==self.pos
        self.moves.append(direction)
        if self.drop_once:
            self.drop_once=False
            return True
        self.pos=max(0,min(self.pages-1,self.pos+direction))
        return True
    def find(self,target):
        return sweep_viewports(self.read,lambda n:n if n==target else None,self.move,lambda n:n)

class ScrollSearchTest(unittest.TestCase):
    def test_late_lesson_found_without_premature_reversal(self):
        v=SimulatedViewport(15)
        result,view=v.find(11)
        self.assertEqual((result,view),(11,11))
        self.assertEqual(v.moves,[1]*11)
    def test_search_can_reverse_at_bottom(self):
        v=SimulatedViewport(9,7)
        result,view=v.find(0)
        self.assertEqual((result,view),(0,0))
        self.assertIn(-1,v.moves)
    def test_single_dropped_swipe_does_not_stop_search(self):
        v=SimulatedViewport(14,drop_once=True)
        self.assertEqual(v.find(12),(12,12))
        self.assertNotIn(-1,v.moves)
    def test_missing_target_is_not_a_pass(self):
        v=SimulatedViewport(8,3)
        result,_=v.find(99)
        self.assertIsNone(result)
        self.assertLessEqual(len(v.moves),62)
        self.assertEqual(v.pos,0)
    def test_no_scrollable_container_still_has_bounded_search(self):
        result,view=sweep_viewports(lambda:0,lambda n:None,lambda t,d:False,lambda t:t)
        self.assertIsNone(result)
        self.assertEqual(view,0)
    def test_falsy_matching_value_is_preserved(self):
        v=SimulatedViewport(10,0)
        self.assertEqual(v.find(0),(0,0))
        self.assertEqual(v.moves,[])

if __name__=="__main__":
    unittest.main(verbosity=2)
