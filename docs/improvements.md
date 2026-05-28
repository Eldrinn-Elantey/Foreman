# Foreman — planned improvements

## Done
- [x] Search button fix: re-render GUI on expand
- [x] Advance status from list (next-status button on row)
- [x] Remove right padding / shrink window to fit
- [x] Fix text alignment in Location field (Show on map label widened)
- [x] Split left/right panels into separate pages for small screens (PagedWidget)
- [x] Page 0: task list / Page N: task detail (opened on row click)
- [x] Prev/next navigation buttons (back button in detail)
- [x] Dynamic GUI height based on screen size
- [x] Scroll in TaskDetailWidget (ListWidget)
- [x] Scrollbar no longer overlaps content (reserved SCROLLBAR_W)
- [x] NEI integration: task icon set via ghost slot drag-and-drop (IconSlotWidget)

## In progress

## Backlog

### UX in task list
- [ ] Delete task directly from list (button on row, no need to open)

### UX in task detail
- [ ] DropdownWidget for status (instead of 3 toggle buttons) — API documented in memory

### Integrations
- [ ] Link item stack to task
- [ ] NEI recipe link
- [ ] Multi-player sync (shared "ender-todo" mode, auto-update for all)
- [ ] Quest integration (HQM / GTNH Quests)
