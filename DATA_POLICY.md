Hack Club Data Policy
=====================

Right now, at HQ, there are a bajillion different places where data is stored. Sprig saves game files in S3, HQ event registrations are spread across Airtable, Google Sheets, and Postgres DBs, club applications are in a separate Airtable DB, Slack activity is in the Slack analytics dashboard, there is stuff all over GitHub, HCB has its own separate Airtable DBs and Postgres, and a bunch more places too.

This has made it very difficult to answer basic questions like: "who are active Hack Clubbers within driving distance of AngelHacks?" or "who came to Epoch and has contributed to Hack Club repos on GitHub?".

We want to make some tools to make this a lot easier. To do this, we'd be pulling data that we already have in various places or that's already public together into one place.

This project got us thinking about Hack Club's lack of a clear data policy in general, and we thought it'd be a good opportunity to start formalizing things.

We're making a public proposal because we want your feedback before making anything official. We believe you should have a say in what we do with this data.

Proposal
--------

Our proposed data policy is simple: we will only use data you explicitly give us (ex. club application form, event registration) or information that's already public and relevant to Hack Club (ex. GitHub PRs, repos) in HQ systems.

We won't use anything that falls within the realm of "metadata". For example, tracking whether or not you specifically clicked a link in an HQ-sent email, or figuring out your specific location by geocoding your IP address that you use to log into Slack.

Example of explicitly giving us data:

-   If you're a club leader, you filled out a form to give us your address. That's how we can send you cool packages! You entered your address in a field and pressed a submit button with the intent of giving us that address, so this counts as explicitly giving us data.

Example of not explicitly giving us data:

-   We won't use geolocated IP logs for form submissions because this was collected implicitly. The same applies for web analytics data, login cookies, and anything else that wasn't submitted to us with the physical intent of doing so.

Some examples of what "public data" is and isn't:

-   When you submit a PR to a public GitHub repo, that's public data because anyone can see it. We might want to be able to ask questions like "who has submitted PRs to Hack Club repos and hasn't been to an HQ-led event yet?"

-   When you log in to Slack, there are access logs available to admins that show IP addresses. This info isn't public and you didn't explicitly give it to us, so it's off limits. We can only use IP addresses for bans.

-   Only publicly available data on Slack (such as information similar to [Slack Analytics](https://hackclub.slack.com/stats)), usually focusing on channels for HQ-led projects, such as Sprig, Haxidraw, and Burrow. The actual content of messages will not be used. Private channels and DMs are always off limits, [like we announced 5 years ago.](https://hackclub.slack.com/archives/C0266FRGT/p1521835388000021)

Notable exceptions / caveats

-   There are certain systems (for example, HCB) that may require long-term logging of IP addresses and other metadata for purposes of security / fraud detection & deterrence / auditing.  This data is and will not be used for marketing purposes, nor would it be conflated with other outside datasets such as what Chronicle uses, but instead is purely intended to ensure engineering stability and compliance with financial standards / laws.

Other aspects of data policy:

-   Outside of core operations (ex. we have to give emails to Stripe for people to get HCB cards mailed to them, or we have to give your email to GitHub to invite you to a HC GitHub repo), Hack Club HQ won't share data with external parties without people opting-in first. Ex. If people want invites for early access to GitHub's new code search, we will ask who wants invites first before sharing emails.

-   Hack Club HQ won't sell data to anyone

We'd like to propose this for a starting place for the Hack Club data policy. It will probably evolve as more HQ systems get built up and we run into more complicated scenarios.
