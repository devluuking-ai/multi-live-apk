# Multi Live APK

ဖုန်းတစ်လုံးတည်းနဲ့ Interactive Live လွှင့်တဲ့ APK — TikTok / Facebook / YouTube သုံးခုလုံးကို RTMP နဲ့ လွှင့်နိုင်ပါတယ်။

## ပါဝင်တာ (v1)

- တရားရိပ်သာ scene (1080×1920) — ကြည့်ရှုသူ avatar တွေ ဝင်ထိုင်၊ comment bubble၊ gift effect၊ ကုသိုလ် စာရင်း၊ ပရိတ်ရွတ်ဖတ်ခြင်း၊ နောက်ခံတီးလုံး + ကြေးစည် + မုဒိတာ အသံ
- RTMP multistream: YouTube + Facebook + TikTok (key ရရင်) တပြိုင်နက်
- Mic + app အသံ ရောပြီး လွှင့် (MixAudioSource)
- စမ်းသပ်ရန် 💬 / 🎁 / 🤖 Bots ခလုတ်များ

## Stream key ယူနည်း

- **YouTube**: YouTube Studio → Create → Go Live → Stream → Stream key ကူးထည့် (Server က ဖြည့်ပြီးသား)
- **Facebook**: facebook.com/live/producer → Go Live → Streaming software → Stream key ကူးထည့် (Page ဖြစ်ရပါမယ်)
- **TikTok**: LIVE setup မှာ "Cast to PC / Stream to PC" ပါမှ Server URL + Key ရပါမယ်။ Key က session တိုင်း အသစ်ဖြစ်ပါတယ်။ RTMP access မရှိရင် TikTok ကို Mobile gaming screen-share နဲ့ သီးသန့်လွှင့်ရပါမယ်။

## မှတ်ချက်

- Platform ၃ ခုလုံး တပြိုင်နက်လွှင့်ရင် upload speed ၃ ဆ လိုပါတယ် (bitrate × ၃)။
- v1 မှာ platform comment/gift အစစ်တွေ မချိတ်ရသေးပါ — sim/bots နဲ့ စမ်းလို့ရပါတယ်။ TikTok/Facebook/YouTube comment ချိတ်တာက နောက် version မှာ ပါပါမယ်။
- Live လွှင့်နေချိန် ဖုန်း screen ကို capture လုပ်တာဖြစ်လို့ app ကို ရှေ့မှာပဲ ထားပါ။ Do Not Disturb ဖွင့်ထားပါ။
