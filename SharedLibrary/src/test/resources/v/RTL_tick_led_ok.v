module tick_light(
	input wire btn_activate,
	input wire VCC,
	input wire GND,
	input wire clk,
	input wire reset,
	output wire red_led
);
	reg next_isActivated;
	reg isActivated;
	localparam
		S0 = 2'b10,
		S1 = 2'b01;
	reg [1:0] next_state;
	reg [1:0] state;
	always @(posedge clk or negedge reset) begin
		if (!reset) begin
			isActivated <= 1'b0;
			state <= S0;
		end else begin
			isActivated <= next_isActivated;
			state <= next_state;
		end
	end
	always @(btn_activate) begin
		next_state = state;
		next_isActivated = isActivated;
		red_led = 1'b0;
		case(state)
			S0: begin
				red_led = VCC;
				if (VCC) begin
					next_state = S1;
				end
			end
			S1: begin
				red_led = GND;
				if (VCC) begin
					next_state = S0;
				end
			end
		endcase
	end
endmodule