local xredis = require('xredis')

local _M = {}

function _M.subscribe(channel, ip, port)
    local red = xredis:new({channel=channel, ip=ip, port=port})
    local func = red:subscribe()
    if not func then
        return nil
    end

    while true do
        local res, err = func()
        if err then
            func(false)
        end
        -- refresh nginx configs here
        ngx.log(ngx.DEBUG, res)
    end

end

function _M.access(server_name, path)
    ngx.log(ngx.DEBUG, "<p>hello, world server_name=" .. server_name .. ", path=" .. path .. "</p>")
    ngx.say("<p>hello, world server_name=" .. server_name .. ", path=" .. path .. "</p>")
end

return _M