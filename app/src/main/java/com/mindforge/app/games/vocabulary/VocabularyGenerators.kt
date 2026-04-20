package com.mindforge.app.games.vocabulary

import com.mindforge.app.domain.model.Difficulty
import com.mindforge.app.domain.model.GameType
import com.mindforge.app.games.MultipleChoiceRound
import kotlin.random.Random

private data class WordSet(
    val word: String,
    val synonym: String,
    val antonym: String,
    val meaning: String,
    val fillSentence: String,
    val fillAnswer: String,
    val typo: String
)

private val vocabularySets = listOf(
    WordSet("Brisk", "Quick", "Slow", "Fast and active", "The morning walk was **____** and refreshing.", "brisk", "brsik"),
    WordSet("Vast", "Huge", "Tiny", "Extremely large", "The desert looked **____** from the hilltop.", "vast", "vst"),
    WordSet("Calm", "Peaceful", "Agitated", "Free from stress or anger", "Her voice stayed **____** during the storm.", "calm", "clam"),
    WordSet("Precise", "Exact", "Vague", "Accurate and careful", "The engineer gave a **____** measurement.", "precise", "preceise"),
    WordSet("Abundant", "Plentiful", "Scarce", "Existing in large quantities", "The harvest was **____** this year.", "abundant", "abundent"),
    WordSet("Durable", "Sturdy", "Fragile", "Able to withstand wear or pressure", "These boots are made of **____** leather.", "durable", "durabel"),
    WordSet("Eager", "Enthusiastic", "Indifferent", "Wanting to do something very much", "The students were **____** to start the project.", "eager", "egar"),
    WordSet("Fragile", "Delicate", "Robust", "Easily broken or damaged", "Please handle this **____** vase with care.", "fragile", "fragail"),
    WordSet("Gloom", "Darkness", "Brightness", "State of partial or total darkness", "The **____** of the forest was mysterious.", "gloom", "glome"),
    WordSet("Humble", "Modest", "Arrogant", "Showing a low estimate of one's importance", "He remained **____** despite his great success.", "humble", "humbel"),
    WordSet("Ignite", "Kindle", "Extinguish", "Catch fire or cause to catch fire", "Use the matches to **____** the campfire.", "ignite", "ignit"),
    WordSet("Jolly", "Cheerful", "Miserable", "Happy and cheerful", "The **____** man laughed loudly.", "jolly", "joly"),
    WordSet("Keen", "Sharp", "Dull", "Highly developed or intense", "Dogs have a **____** sense of smell.", "keen", "kene"),
    WordSet("Lofty", "Towering", "Lowly", "Of imposing height", "The **____** peaks were covered in snow.", "lofty", "loftey"),
    WordSet("Mend", "Repair", "Break", "Fix something that is broken", "I need to **____** the hole in my sock.", "mend", "mende"),
    WordSet("Nimble", "Agile", "Clumsy", "Quick and light in movement", "The **____** cat jumped onto the shelf.", "nimble", "nimbul"),
    WordSet("Obscure", "Unclear", "Obvious", "Not discovered or known about", "The meaning of the poem was **____**.", "obscure", "obskure"),
    WordSet("Ponder", "Consider", "Ignore", "Think about something carefully", "She sat down to **____** her future.", "ponder", "pondr"),
    WordSet("Quaint", "Charming", "Modern", "Attractively unusual or old-fashioned", "They lived in a **____** little cottage.", "quaint", "qaint"),
    WordSet("Rigid", "Stiff", "Flexible", "Unable to bend or be forced out of shape", "The rules of the game are **____**.", "rigid", "rijid"),
    WordSet("Shrewd", "Astute", "Foolish", "Having sharp powers of judgment", "He made a **____** investment in the company.", "shrewd", "shred"),
    WordSet("Thrive", "Prosper", "Decline", "Grow or develop well or vigorously", "Plants **____** in rich, moist soil.", "thrive", "thriv"),
    WordSet("Utter", "Complete", "Partial", "Complete; absolute", "The room was in **____** chaos.", "utter", "uttr"),
    WordSet("Vague", "Indistinct", "Clear", "Of uncertain or unclear character", "He gave a **____** description of the man.", "vague", "vage"),
    WordSet("Wary", "Cautious", "Trusting", "Feeling or showing caution", "Be **____** of strangers offering gifts.", "wary", "warrey"),
    WordSet("Yield", "Produce", "Withhold", "Produce or provide", "The apple trees **____** a lot of fruit.", "yield", "yeild"),
    WordSet("Zest", "Enthusiasm", "Apathy", "Great enthusiasm and energy", "She has a real **____** for life.", "zest", "zst"),
    WordSet("Adept", "Skilled", "Inept", "Very skilled or proficient", "He is **____** at playing the piano.", "adept", "adepted"),
    WordSet("Bliss", "Happiness", "Misery", "Perfect happiness; great joy", "Relaxing on the beach was pure **____**.", "bliss", "blis"),
    WordSet("Cunning", "Sly", "Naive", "Having or showing skill in deceit", "The **____** fox tricked the crow.", "cunning", "cuning"),
    WordSet("Dread", "Fear", "Anticipation", "Anticipate with great apprehension", "I **____** having to go to the dentist.", "dread", "dred"),
    WordSet("Elite", "Superior", "Ordinary", "A select group that is superior", "Only the **____** athletes make the team.", "elite", "elete"),
    WordSet("Flaw", "Defect", "Perfection", "A mark, fault, or imperfection", "There is a tiny **____** in this diamond.", "flaw", "flo"),
    WordSet("Gaze", "Stare", "Glance", "Look steadily and intently", "They sat and **____** at the stars.", "gaze", "gaize"),
    WordSet("Halt", "Stop", "Continue", "Bring or come to an abrupt stop", "The project was brought to a **____**.", "halt", "hault"),
    WordSet("Idle", "Inactive", "Busy", "Not active or in use", "The factory machines stood **____**.", "idle", "idel"),
    WordSet("Jolt", "Shake", "Soothe", "Push or shake abruptly", "The bus gave a sudden **____**.", "jolt", "jowlt"),
    WordSet("Knack", "Talent", "Inability", "An acquired or natural skill", "She has a **____** for remembering names.", "knack", "nack"),
    WordSet("Lush", "Luxuriant", "Barren", "Growing luxuriantly", "The garden was **____** with green plants.", "lush", "luss"),
    WordSet("Mute", "Silent", "Vocal", "Refraining from speech or quiet", "The audience sat in **____** amazement.", "mute", "muit"),
    WordSet("Novel", "New", "Traditional", "New or unusual in an interesting way", "That is a **____** approach to the problem.", "novel", "noval"),
    WordSet("Omen", "Sign", "Coincidence", "An event regarded as a portent of good or evil", "They thought the bird was a good **____**.", "omen", "omin"),
    WordSet("Peak", "Summit", "Base", "The pointed top of a mountain", "We reached the **____** at noon.", "peak", "peek"),
    WordSet("Query", "Question", "Answer", "A question, especially one expressing doubt", "If you have a **____**, please ask.", "query", "querey"),
    WordSet("Rare", "Uncommon", "Frequent", "Not occurring very often", "It is **____** to see snow in the desert.", "rare", "rair"),
    WordSet("Swift", "Fast", "Sluggish", "Moving or capable of moving with great speed", "The eagle made a **____** descent.", "swift", "swif"),
    WordSet("Toil", "Labor", "Relax", "Work extremely hard or incessantly", "They had to **____** in the fields all day.", "toil", "toyl"),
    WordSet("Urge", "Encourage", "Discourage", "Try earnestly or persistently to persuade", "I **____** you to reconsider your decision.", "urge", "erge"),
    WordSet("Vivid", "Graphic", "Faded", "Producing powerful feelings or strong images", "He gave a **____** account of his travels.", "vivid", "vividly"),
    WordSet("Witty", "Humorous", "Serious", "Showing or characterized by quick and inventive verbal humor", "She is known for her **____** remarks.", "witty", "wity"),
    WordSet("Acute", "Sharp", "Dull", "Present or experienced to a severe or intense degree", "He felt an **____** pain in his chest.", "acute", "acut"),
    WordSet("Barren", "Unproductive", "Fertile", "Too poor to produce much or any vegetation", "The landscape was **____** and lifeless.", "barren", "baron"),
    WordSet("Cordial", "Friendly", "Unfriendly", "Warm and friendly", "We received a **____** welcome.", "cordial", "cordel"),
    WordSet("Diverse", "Various", "Similar", "Showing a great deal of variety", "The city has a **____** population.", "diverse", "divurse"),
    WordSet("Eloquent", "Persuasive", "Inarticulate", "Fluent or persuasive in speaking or writing", "The president gave an **____** speech.", "eloquent", "eloquent"),
    WordSet("Frugal", "Thrifty", "Extravagant", "Sparing or economical with regard to money", "He led a **____** life to save money.", "frugal", "frugel"),
    WordSet("Genuine", "Authentic", "Fake", "Truly what something is said to be", "This is a **____** leather jacket.", "genuine", "genine"),
    WordSet("Hostile", "Antagonistic", "Friendly", "Unfriendly; antagonistic", "The crowd was **____** to the visitors.", "hostile", "hostal"),
    WordSet("Immense", "Huge", "Small", "Extremely large or great", "The pressure was **____**.", "immense", "imence"),
    WordSet("Jovial", "Cheerful", "Gloomy", "Cheerful and friendly", "The host was in a **____** mood.", "jovial", "jovel"),
    WordSet("Kinship", "Relationship", "Estrangement", "Blood relationship", "They felt a strong **____** with each other.", "kinship", "kinshipp"),
    WordSet("Luminous", "Bright", "Dark", "Full of or shedding light", "The moon cast a **____** glow.", "luminous", "luminus"),
    WordSet("Meager", "Small", "Abundant", "Lacking in quantity or quality", "The villagers had a **____** harvest.", "meager", "meagre"),
    WordSet("Nebulous", "Hazy", "Clear", "In the form of a cloud or haze; hazy", "His plans were still **____**.", "nebulous", "nebulus"),
    WordSet("Obsolete", "Outdated", "Modern", "No longer produced or used", "The machinery was **____**.", "obsolete", "obsolet"),
    WordSet("Plausible", "Reasonable", "Unlikely", "Seeming reasonable or probable", "That is a **____** explanation.", "plausible", "plausabel"),
    WordSet("Quell", "Suppress", "Incite", "Put an end to a disorder", "The police were able to **____** the riot.", "quell", "quel"),
    WordSet("Resilient", "Strong", "Fragile", "Able to withstand or recover quickly", "The economy was **____**.", "resilient", "resiliant"),
    WordSet("Subtle", "Understated", "Obvious", "So delicate or precise as to be difficult to analyze", "There was a **____** change in her tone.", "subtle", "subtel"),
    WordSet("Trivial", "Unimportant", "Significant", "Of little value or importance", "The difference is **____**.", "trivial", "trivyal"),
    WordSet("Unique", "Distinctive", "Common", "Being the only one of its kind", "Each person has a **____** fingerprint.", "unique", "uniq"),
    WordSet("Vex", "Annoy", "Soothe", "Make someone feel annoyed or frustrated", "The constant noise began to **____** him.", "vex", "veks"),
    WordSet("Wander", "Roam", "Stay", "Walk or move in a leisurely or aimless way", "They liked to **____** through the woods.", "wander", "wandr"),
    WordSet("Yell", "Shout", "Whisper", "A loud, sharp cry of pain or surprise", "He had to **____** to be heard.", "yell", "yel"),
    WordSet("Zeal", "Passion", "Indifference", "Great energy or enthusiasm", "His **____** for the project was contagious.", "zeal", "zeel"),
    WordSet("Antique", "Ancient", "Modern", "A collectible object such as a piece of furniture", "The shop sold **____** furniture.", "antique", "antike"),
    WordSet("Brittle", "Fragile", "Flexible", "Hard but liable to break or shatter easily", "The dry leaves were **____**.", "brittle", "britle"),
    WordSet("Clarity", "Clearness", "Confusion", "The quality of being clear", "The lawyer spoke with **____**.", "clarity", "clarety"),
    WordSet("Docile", "Compliant", "Stubborn", "Ready to accept control or instruction", "The dog was very **____**.", "docile", "dosile"),
    WordSet("Exotic", "Foreign", "Native", "Originating in or characteristic of a distant foreign country", "The island was full of **____** birds.", "exotic", "exotick"),
    WordSet("Fierce", "Intense", "Mild", "Having or displaying an intense or ferocious aggressiveness", "The storm was **____**.", "fierce", "fearce"),
    WordSet("Graft", "Connect", "Separate", "A shoot or twig inserted into a slit on the trunk or stem of a living plant", "The surgeon had to **____** skin onto the wound.", "graft", "grafte"),
    WordSet("Honest", "Truthful", "Dishonest", "Free of deceit and untruthful", "He is a very **____** person.", "honest", "onest"),
    WordSet("Impact", "Influence", "Insignificance", "The action of one object coming forcibly into contact with another", "The news had a huge **____**.", "impact", "impakt"),
    WordSet("Justice", "Fairness", "Injustice", "Just behavior or treatment", "They fought for **____**.", "justice", "justiss"),
    WordSet("Kindle", "Ignite", "Extinguish", "Light or set on fire", "They used dry wood to **____** the fire.", "kindle", "kindel"),
    WordSet("Lethal", "Deadly", "Harmless", "Sufficient to cause death", "The poison was **____**.", "lethal", "lethel"),
    WordSet("Motive", "Reason", "Deterrent", "A reason for doing something", "What was his **____** for the crime?", "motive", "motiv"),
    WordSet("Nimble", "Agile", "Clumsy", "Quick and light in movement or action", "Her **____** fingers flew across the keyboard.", "nimble", "nimbul"),
    WordSet("Optimum", "Ideal", "Worst", "Most favorable", "These are the **____** conditions for growth.", "optimum", "optimun"),
    WordSet("Ponder", "Consider", "Ignore", "Think about something carefully", "I need time to **____** my options.", "ponder", "pondr"),
    WordSet("Quiver", "Tremble", "Steady", "Tremble or shake with a slight rapid motion", "The leaves began to **____** in the breeze.", "quiver", "quivr"),
    WordSet("Rustic", "Rural", "Urban", "Relating to the countryside; rural", "They stayed in a **____** cabin.", "rustic", "rustick"),
    WordSet("Sturdy", "Strong", "Weak", "Strongly and solidly built", "The table was very **____**.", "sturdy", "sturdey"),
    WordSet("Tenets", "Beliefs", "Doubts", "A principle or belief", "The **____** of the religion are simple.", "tenets", "tennets"),
    WordSet("Uphold", "Maintain", "Violate", "Confirm or support", "The judge decided to **____** the law.", "uphold", "uphould"),
    WordSet("Vigor", "Energy", "Lethargy", "Physical strength and good health", "He worked with great **____**.", "vigor", "vigour"),
    WordSet("Wealth", "Riches", "Poverty", "An abundance of valuable possessions or money", "The family has great **____**.", "wealth", "welth"),
    WordSet("Yield", "Produce", "Withhold", "Produce or provide", "The investment will **____** high returns.", "yield", "yeild"),
    WordSet("Zenith", "Peak", "Nadir", "The time at which something is most powerful or successful", "The Roman Empire was at its **____**.", "zenith", "zeneth"),
    WordSet("Baffle", "Perplex", "Clarify", "Totally bewilder or perplex", "The puzzle continued to **____** them.", "baffle", "baffel"),
    WordSet("Candid", "Truthful", "Secretive", "Truthful and straightforward", "She was very **____** about her mistakes.", "candid", "kandid"),
    WordSet("Delete", "Remove", "Add", "Remove or obliterate", "I had to **____** the file.", "delete", "delet"),
    WordSet("Escort", "Accompany", "Abandon", "A person, vehicle, or ship accompanying another", "The queen was given a military **____**.", "escort", "eskort"),
    WordSet("Flamboyant", "Showy", "Modest", "Tending to attract attention", "The singer was known for his **____** costumes.", "flamboyant", "flamboyent"),
    WordSet("Gratify", "Please", "Disappoint", "Give someone pleasure or satisfaction", "It did not **____** her to see him fail.", "gratify", "gratifie"),
    WordSet("Heed", "Notice", "Ignore", "Pay attention to", "You should **____** my warning.", "heed", "hede"),
    WordSet("Insight", "Understanding", "Ignorance", "A deep understanding of a person or thing", "He has great **____** into human nature.", "insight", "insite"),
    WordSet("Jaunt", "Trip", "Stay", "A short excursion or journey for pleasure", "They went for a **____** in the car.", "jaunt", "jawnt"),
    WordSet("Karma", "Fate", "Chance", "The sum of a person's actions in this and previous states of existence", "He believes in **____**.", "karma", "carma"),
    WordSet("Legend", "Myth", "Fact", "A traditional story sometimes popularly regarded as historical", "The story of King Arthur is a **____**.", "legend", "legand"),
    WordSet("Metric", "Measure", "Guess", "A system or standard of measurement", "The company uses several **____** to track success.", "metric", "metrick"),
    WordSet("Nurture", "Care", "Neglect", "Care for and encourage the growth of", "Parents should **____** their children.", "nurture", "nurchur"),
    WordSet("Origin", "Source", "End", "The point or place where something begins", "The **____** of the river is in the mountains.", "origin", "origun"),
    WordSet("Portal", "Entrance", "Exit", "A doorway, gate, or other entrance", "They stepped through the **____**.", "portal", "portel"),
    WordSet("Quash", "Suppress", "Promote", "Reject or void, especially by legal procedure", "The government tried to **____** the protest.", "quash", "kwash"),
    WordSet("Random", "Unpredictable", "Systematic", "Made, done, happening, or chosen without method or conscious decision", "The samples were chosen at **____**.", "random", "randum"),
    WordSet("Social", "Communal", "Solitary", "Relating to society or its organization", "Humans are **____** animals.", "social", "soshal"),
    WordSet("Target", "Goal", "Avoid", "A person, object, or place selected as the aim of an attack", "The arrow hit the **____**.", "target", "targit"),
    WordSet("Urgent", "Pressing", "Trivial", "Requiring immediate action or attention", "This is an **____** matter.", "urgent", "urgant"),
    WordSet("Volume", "Quantity", "Silence", "A book forming part of a work or series", "The library has a huge **____** of books.", "volume", "volum"),
    WordSet("Wiggle", "Squirm", "Still", "Move or cause to move up and down or from side to side", "The puppy began to **____** with excitement.", "wiggle", "wigel"),
    WordSet("Xenon", "Gas", "Solid", "The chemical element of atomic number 54", "The lamp uses **____** gas.", "xenon", "zenon"),
    WordSet("Yearn", "Long", "Dislike", "Have an intense feeling of longing for something", "They **____** for the day they could return home.", "yearn", "yern"),
    WordSet("Zigzag", "Crooked", "Straight", "A line or course having abrupt alternate right and left turns", "The path began to **____** up the mountain.", "zigzag", "zig-zag"),
    WordSet("Adapt", "Adjust", "Preserve", "Make something suitable for a new use or purpose", "You must learn to **____** to new situations.", "adapt", "adept"),
    WordSet("Blast", "Explosion", "Whiff", "A destructive wave of highly compressed air", "The **____** could be heard for miles.", "blast", "blaste"),
    WordSet("Cosmic", "Universal", "Terrestrial", "Relating to the universe or cosmos", "The telescope captured **____** images.", "cosmic", "kosmik"),
    WordSet("Detach", "Separate", "Attach", "Disconnect and remove", "Please **____** the coupon and send it back.", "detach", "detatch"),
    WordSet("Exhale", "Breathe out", "Inhale", "Breathe out", "You should **____** slowly.", "exhale", "exhale"),
    WordSet("Fabric", "Cloth", "Steel", "Cloth produced by weaving or knitting fibers", "The curtains were made of heavy **____**.", "fabric", "fabrick"),
    WordSet("Gallop", "Run", "Crawl", "The fastest pace of a horse", "The horses began to **____** across the field.", "gallop", "galop"),
    WordSet("Hollow", "Empty", "Solid", "Having a hole or empty space inside", "The log was **____**.", "hollow", "holow"),
    WordSet("Impact", "Effect", "Void", "The action of one object coming forcibly into contact with another", "The collision had a massive **____**.", "impact", "impakt"),
    WordSet("Jungle", "Forest", "Desert", "An area of land overgrown with dense forest and tangled vegetation", "The tigers live in the **____**.", "jungle", "jungel"),
    WordSet("Kernel", "Core", "Shell", "A softer, usually edible part of a nut", "There was a **____** of truth in his story.", "kernel", "kernal"),
    WordSet("Launch", "Start", "Stop", "Set a boat or ship in motion", "The company will **____** its new product tomorrow.", "launch", "lawnch"),
    WordSet("Manual", "Hand-operated", "Automatic", "Relating to or done with the hands", "He had to do **____** labor.", "manual", "manuel"),
    WordSet("Nearby", "Close", "Distant", "Close at hand", "Is there a gas station **____**?", "nearby", "nerby"),
    WordSet("Outset", "Beginning", "Conclusion", "The start or beginning of something", "From the **____**, we knew it would be difficult.", "outset", "outsett"),
    WordSet("Pledge", "Promise", "Refusal", "A solemn promise or undertaking", "The students took a **____** of silence.", "pledge", "plege"),
    WordSet("Quartz", "Mineral", "Liquid", "A hard white or colorless mineral", "The watch was made of **____**.", "quartz", "kwartz"),
    WordSet("Radius", "Distance", "Center", "A straight line from the center to the circumference", "The park is within a five-mile **____**.", "radius", "radious"),
    WordSet("Search", "Hunt", "Find", "Try to find something by looking or otherwise seeking carefully", "The police began a **____** for the missing child.", "search", "serch"),
    WordSet("Thirst", "Dehydration", "Fullness", "A feeling of needing or wanting to drink something", "He had a great **____** after the race.", "thirst", "thurst"),
    WordSet("Update", "Modernize", "Ignore", "Make something more modern", "I need to **____** my computer.", "update", "up-date"),
    WordSet("Vendor", "Seller", "Buyer", "A person or company offering something for sale", "The ice cream **____** was on the corner.", "vendor", "vender"),
    WordSet("Wisdom", "Knowledge", "Folly", "The quality of having experience, knowledge, and good judgment", "He is a man of great **____**.", "wisdom", "wizdom"),
    WordSet("Yield", "Surrender", "Resist", "Give way to arguments, demands, or pressure", "He finally had to **____** to their demands.", "yield", "yeild"),
    WordSet("Zone", "Area", "Point", "An area or stretch of land", "The school is in a quiet **____**.", "zone", "zon"),
    WordSet("Ascent", "Climb", "Descent", "A rise or climb to a higher point", "The **____** of the mountain was grueling.", "ascent", "assent"),
    WordSet("Blend", "Mix", "Separate", "Mix together different elements", "You should **____** the ingredients thoroughly.", "blend", "blende"),
    WordSet("Crude", "Unrefined", "Sophisticated", "In a natural or raw state", "The device was very **____**.", "crude", "crud"),
    WordSet("Draft", "Sketch", "Final", "A preliminary version of a piece of writing", "He wrote a first **____** of his novel.", "draft", "drafte"),
    WordSet("Exert", "Apply", "Relax", "Apply or bring to bear a force", "You need to **____** more effort.", "exert", "exurt"),
    WordSet("Flora", "Plants", "Fauna", "The plants of a particular region", "The island has unique **____**.", "flora", "florah"),
    WordSet("Grasp", "Clutch", "Release", "Seize and hold firmly", "He tried to **____** the rope.", "grasp", "graspe"),
    WordSet("Haste", "Hurry", "Slowness", "Excessive speed or urgency of movement", "In her **____**, she forgot her keys.", "haste", "haiste"),
    WordSet("Index", "Catalog", "Chaos", "An alphabetical list of names or subjects", "The **____** is at the back of the book.", "index", "indeks"),
    WordSet("Joint", "Combined", "Separate", "A point at which parts of an artificial structure are joined", "The project was a **____** effort.", "joint", "joynt"),
    WordSet("Lapse", "Failure", "Continuity", "A temporary failure of concentration, memory, or judgment", "He had a **____** in memory.", "lapse", "laps"),
    WordSet("Model", "Example", "Copy", "A three-dimensional representation of a person or thing", "He built a scale **____** of the ship.", "model", "moddel"),
    WordSet("Nerve", "Courage", "Fear", "A whitish fiber or bundle of fibers in the body", "He didn't have the **____** to jump.", "nerve", "nerv"),
    WordSet("Ocean", "Sea", "Pond", "A very large expanse of sea", "The **____** was calm today.", "ocean", "oshun"),
    WordSet("Panic", "Alarm", "Calm", "Sudden uncontrollable fear or anxiety", "The noise caused a **____**.", "panic", "panick"),
    WordSet("Quota", "Allowance", "Excess", "A fixed share of something", "The factory met its production **____**.", "quota", "quowta"),
    WordSet("Realm", "Kingdom", "Void", "A kingdom", "The king ruled over a vast **____**.", "realm", "relm"),
    WordSet("Shift", "Move", "Stationary", "Move or cause to move from one place to another", "The wind began to **____**.", "shift", "shifte"),
    WordSet("Trait", "Characteristic", "Anomaly", "A distinguishing quality or characteristic", "Honesty is a good **____**.", "trait", "trate"),
    WordSet("Unity", "Oneness", "Division", "The state of being united or joined as a whole", "The country needs **____**.", "unity", "unety"),
    WordSet("Vault", "Chamber", "Open", "A large room or chamber used for storage", "The money is kept in a **____**.", "vault", "vawlt"),
    WordSet("Waste", "Squander", "Save", "Use or expend carelessly or to no purpose", "Don't **____** your time.", "waste", "waiste"),
    WordSet("Young", "Youthful", "Old", "Having lived or existed for only a short time", "The **____** man was very ambitious.", "young", "yung"),
    WordSet("Amuse", "Entertain", "Bore", "Provide interesting and enjoyable occupation", "The movie did **____** the children.", "amuse", "amuze"),
    WordSet("Batch", "Group", "Individual", "A quantity or consignment of goods produced at one time", "She baked a **____** of cookies.", "batch", "bach"),
    WordSet("Cargo", "Freight", "Void", "Goods carried on a ship, aircraft, or motor vehicle", "The ship was carrying a heavy **____**.", "cargo", "kargo"),
    WordSet("Doubt", "Uncertainty", "Certainty", "A feeling of uncertainty or lack of conviction", "I have no **____** that he will win.", "doubt", "dowbt"),
    WordSet("Entry", "Entrance", "Exit", "An act of going or coming in", "His **____** into the room was silent.", "entry", "entrey"),
    WordSet("Frame", "Structure", "Void", "A rigid structure that surrounds or encloses something", "The window **____** was made of wood.", "frame", "fraim"),
    WordSet("Greet", "Welcome", "Ignore", "Give a polite word or sign of welcome", "He was there to **____** the guests.", "greet", "grete"),
    WordSet("Habit", "Routine", "Occasional", "A settled or regular tendency or practice", "He has a **____** of biting his nails.", "habit", "habbet"),
    WordSet("Image", "Picture", "Reality", "A representation of the external form of a person or thing", "The mirror reflected his **____**.", "image", "imaje"),
    WordSet("Judge", "Evaluate", "Ignore", "A public official appointed to decide cases in a court of law", "The **____** listened to both sides.", "judge", "juge"),
    WordSet("Large", "Big", "Small", "Of considerable or relatively great size", "The elephant was very **____**.", "large", "larj"),
    WordSet("Match", "Couple", "Mismatch", "A contest in which people or teams compete", "The football **____** was exciting.", "match", "mach"),
    WordSet("Night", "Darkness", "Day", "The period from sunset to sunrise", "The **____** was very cold.", "night", "nite"),
    WordSet("Order", "Sequence", "Chaos", "The arrangement or disposition of people or things", "Please put the books in **____**.", "order", "ordr"),
    WordSet("Pride", "Satisfaction", "Shame", "A feeling of deep pleasure or satisfaction derived from one's own achievements", "He took great **____** in his work.", "pride", "pryde"),
    WordSet("Quick", "Fast", "Slow", "Moving fast or doing something in a short time", "She gave a **____** reply.", "quick", "quik"),
    WordSet("Rough", "Uneven", "Smooth", "Having an uneven or irregular surface", "The road was very **____**.", "rough", "ruff"),
    WordSet("Stone", "Rock", "Liquid", "Hard solid nonmetallic mineral matter", "He threw a **____** into the water.", "stone", "stoun"),
    WordSet("Trust", "Confidence", "Suspicion", "Firm belief in the reliability, truth, ability, or strength of someone or something", "I have complete **____** in her.", "trust", "truste"),
    WordSet("Usage", "Utilization", "Neglect", "The action of using something or the fact of being used", "The **____** of the new system is mandatory.", "usage", "useage"),
    WordSet("Voice", "Sound", "Silence", "The sound produced in a person's larynx", "She has a very loud **____**.", "voice", "voyce"),
    WordSet("Whole", "Entire", "Partial", "All of; entire", "I ate the **____** pizza.", "whole", "hole"),
    WordSet("Yield", "Cede", "Maintain", "Produce or provide a natural, agricultural, or industrial product", "The trees **____** a lot of fruit.", "yield", "yeild"),
    WordSet("Adore", "Love", "Hate", "Love and respect someone deeply", "They **____** their new baby.", "adore", "adore"),
    WordSet("Begin", "Start", "End", "Perform or undergo the first part of an action or activity", "The show will **____** at eight o'clock.", "begin", "beggin"),
    WordSet("Clear", "Transparent", "Cloudy", "Easy to perceive, understand, or interpret", "The water was very **____**.", "clear", "cleer"),
    WordSet("Drive", "steer", "Stop", "Operate and control the direction and speed of a motor vehicle", "He likes to **____** fast.", "drive", "driv"),
    WordSet("Empty", "Vacant", "Full", "Containing nothing; not filled or occupied", "The bottle was **____**.", "empty", "emty"),
    WordSet("Fresh", "New", "Stale", "Recently made or obtained", "I bought some **____** bread.", "fresh", "freshe"),
    WordSet("Grand", "Magnificent", "Insignificant", "Magnificent and imposing in appearance", "The palace was very **____**.", "grand", "grande"),
    WordSet("Heavy", "Weighty", "Light", "Of great weight", "The box was too **____** to carry.", "heavy", "hevey"),
    WordSet("Issue", "Problem", "Solution", "An important topic or problem for debate or discussion", "We need to resolve this **____**.", "issue", "isshew"),
    WordSet("Learn", "Study", "Forget", "Acquire knowledge of or skill in by study", "He wants to **____** French.", "learn", "lurn"),
    WordSet("Music", "Melody", "Noise", "Vocal or instrumental sounds combined in such a way as to produce beauty of form", "I love listening to **____**.", "music", "musick"),
    WordSet("Never", "Not at all", "Always", "At no time in the past or future", "I have **____** been to Paris.", "never", "nevr"),
    WordSet("Paint", "Color", "Clean", "A colored substance which is spread over a surface", "The artist began to **____** the landscape.", "paint", "pante"),
    WordSet("Quiet", "Silent", "Loud", "Making little or no noise", "The library was very **____**.", "quiet", "quitet"),
    WordSet("River", "Stream", "Mountain", "A large natural stream of water flowing in a channel to the sea", "The **____** flows through the city.", "river", "rivver"),
    WordSet("Solid", "Firm", "Liquid", "Firm and stable in shape", "The ice was **____** enough to walk on.", "solid", "solled"),
    WordSet("Train", "Teach", "Ignore", "Teach a person or animal a particular skill", "He needs to **____** his dog.", "train", "trane"),
    WordSet("Visit", "Sightsee", "Avoid", "Go to see and spend time with", "They went to **____** their grandparents.", "visit", "vissit"),
    WordSet("Write", "Inscribe", "Erase", "Mark letters, words, or other symbols on a surface", "She began to **____** a letter.", "write", "rite")
)

interface VocabularyQuestionGenerator {
    fun synonymAntonym(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound
    fun wordMeaning(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound
    fun fillInBlank(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound
    fun spellingCorrection(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound
}

class OfflineVocabularyQuestionGenerator : VocabularyQuestionGenerator {
    override fun synonymAntonym(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound {
        val set = vocabularySets.random(random)
        val askForSynonym = random.nextBoolean()
        val correct = if (askForSynonym) set.synonym else set.antonym
        val distractors = vocabularySets.map {
            if (askForSynonym) it.synonym else it.antonym
        }.filterNot { it == correct }.distinct().shuffled(random).take(3)
        val options = (distractors + correct).shuffled(random)
        return MultipleChoiceRound(
            id = "syn-ant-$index",
            gameType = GameType.SYNONYM_ANTONYM,
            prompt = "Choose the **${if (askForSynonym) "synonym" else "antonym"}** for \"**${set.word}**\".",
            timeLimitSeconds = if (difficulty == Difficulty.HARD) 9 else 12,
            options = options,
            correctIndex = options.indexOf(correct)
        )
    }

    override fun wordMeaning(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound {
        val set = vocabularySets.random(random)
        val distractors = vocabularySets.map { it.meaning }.filterNot { it == set.meaning }.distinct().shuffled(random).take(3)
        val options = (distractors + set.meaning).shuffled(random)
        return MultipleChoiceRound(
            id = "meaning-$index",
            gameType = GameType.WORD_MEANING,
            prompt = "What does \"**${set.word}**\" mean?",
            timeLimitSeconds = if (difficulty == Difficulty.HARD) 10 else 13,
            options = options,
            correctIndex = options.indexOf(set.meaning)
        )
    }

    override fun fillInBlank(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound {
        val set = vocabularySets.random(random)
        val distractors = vocabularySets.map { it.fillAnswer }.filterNot { it == set.fillAnswer }.distinct().shuffled(random).take(3)
        val options = (distractors + set.fillAnswer).shuffled(random)
        return MultipleChoiceRound(
            id = "blank-$index",
            gameType = GameType.FILL_IN_THE_BLANK,
            prompt = set.fillSentence,
            timeLimitSeconds = if (difficulty == Difficulty.HARD) 9 else 11,
            options = options.map(::titleCaseWord),
            correctIndex = options.indexOf(titleCaseWord(set.fillAnswer))
        )
    }

    override fun spellingCorrection(index: Int, difficulty: Difficulty, random: Random): MultipleChoiceRound {
        val set = vocabularySets.random(random)
        val distractors = listOf(set.typo, set.word.drop(1), set.word.reversed()).shuffled(random)
        val correct = titleCaseWord(set.word)
        val options = (distractors.map(::titleCaseWord) + correct).shuffled(random)
        return MultipleChoiceRound(
            id = "spelling-$index",
            gameType = GameType.SPELLING_CORRECTION,
            prompt = "Select the correctly spelled word.",
            timeLimitSeconds = if (difficulty == Difficulty.HARD) 8 else 11,
            options = options,
            correctIndex = options.indexOf(correct),
            supportingText = "Look closely for **swapped letters** and **missing vowels**."
        )
    }
}

private fun titleCaseWord(value: String): String =
    value.replaceFirstChar { character -> character.uppercase() }
