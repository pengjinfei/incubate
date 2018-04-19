---
--- Generated by EmmyLua(https://github.com/EmmyLua)
--- Created by jinfei.
--- DateTime: 4/19/18 21:47
---
local zk=KEYS[1]
local lk=KEYS[2]
local num = tonumber(KEYS[3])
local topLevel = tonumber(KEYS[4])
local val = redis.call('llen', lk)
val = topLevel - val
if (val <= 0) then
    return true
end
if (num >= val) then
    num = val
end
local res={}
res = redis.call('zrange',zk, 0,num-1)
for sk,k in ipairs(res) do
    redis.call('rpush',lk,k)
end
if(table.getn(res)>0) then
    redis.call('ZREMRANGEBYRANK',zk,0,num-1)
end
local t=redis.call('llen', lk)
return true;