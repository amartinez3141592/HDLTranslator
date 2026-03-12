module parallel_to_serial(
	input wire [2:0] parallel,
	input wire clk,
	input wire reset,
	output wire serial
);
	reg [2:0] aux;
	reg [2:0] next_aux;
	localparam
		S0 = 3'b100,
		S1 = 3'b010,
		S2 = 3'b001;
	reg [2:0] next_state;
	reg [2:0] state;
	always @(( posedge clk or negedge reset )) begin
		if (!(reset)) begin
			aux <= 3'b000;
			state <= S0;
		end else begin
			aux <= next_aux;
			state <= next_state;
		end
	end
	always @(parallel or aux or state) begin
		next_state = state;
		next_aux = aux;
		serial = 1'b0;
		case(state)
			S0: begin
				next_aux = {parallel[2],parallel[1],parallel[0]};
				serial = aux[0];
				if (1) begin
					next_state = S1;
				end
			end
			S1: begin
				next_aux = {parallel[0],parallel[2],parallel[1]};
				serial = aux[0];
				if (1) begin
					next_state = S2;
				end
			end
			S2: begin
				next_aux = {parallel[1],parallel[0],parallel[2]};
				serial = aux[0];
				if (1) begin
					next_state = S0;
				end
			end
		endcase
	end
endmodule