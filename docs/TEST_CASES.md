# Manual test checklist

| ID | Test | Expected result |
|---|---|---|
| TC-01 | Start the application with valid MySQL settings | Dashboard opens without a database error |
| TC-02 | Launch against an empty database | Required tables are created and sample menu items appear |
| TC-03 | Add a menu item | The item appears in Menu management |
| TC-04 | Edit a menu item's price | The new price appears in the menu |
| TC-05 | Mark an item unavailable | It remains in the menu table but is omitted from New Order |
| TC-06 | Delete a menu item | It disappears from the menu; previous order snapshots remain |
| TC-07 | Add or edit a customer | Customer details appear in the directory and checkout selector |
| TC-08 | Add two menu items with quantities | Cart rows and subtotal update correctly |
| TC-09 | Complete an order | Order and order lines are committed to MySQL |
| TC-10 | Complete an order | A receipt text file is created in `receipts/` |
| TC-11 | Open Order History and view an order | Customer, date, line items, subtotal, tax and total are displayed |
| TC-12 | Open Reports | Completed sales ledger and summary figures are displayed |
| TC-13 | Enter an invalid price in Menu management | A friendly validation message appears |
| TC-14 | Start without MySQL running | A clear database setup message appears instead of a stack trace |

Automated JUnit tests cover subtotal calculation, tax calculation, rounding, and invalid billing input. Database/UI integration tests require a configured MySQL instance and are part of the manual checklist above.
