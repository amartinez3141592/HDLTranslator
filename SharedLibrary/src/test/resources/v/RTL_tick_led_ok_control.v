module tick_light(
	input wire clk,
	input wire reset,
	output wire red_led
);
	reg value_led;
	reg next_value_led;
	always @(posedge clk or negedge reset) begin
		if (!(reset)) begin
			value_led <= 1'b0;
		end else begin
			value_led <= next_value_led;
		end
	end
	always @(value_led) begin
		red_led = value_led;
		next_value_led = !(value_led);
	end
endmodule