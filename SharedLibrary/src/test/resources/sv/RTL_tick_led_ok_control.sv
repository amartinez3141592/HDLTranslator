module tick_light(
	input logic clk,
	input logic reset,
	output logic red_led
);
	logic value_led;
	logic next_value_led;
	always_ff @( posedge clk or negedge reset ) begin
		if (!(reset)) begin
			value_led <= 1'b0;
		end else begin
			value_led <= next_value_led;
		end
	end
	always_comb begin 
		red_led=value_led;
		next_value_led=!(value_led);
	end
endmodule
